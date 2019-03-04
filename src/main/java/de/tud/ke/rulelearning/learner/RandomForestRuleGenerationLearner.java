package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.RuleGenerationConfiguration;
import de.tud.ke.rulelearning.model.*;
import de.tud.ke.rulelearning.util.ReflectionUtil;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.data.MultiLabelInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.TechnicalInformation;

public class RandomForestRuleGenerationLearner extends AbstractRuleGenerationLearner {

    private class TreeParser extends RandomTree {

        private final Instances info;

        private final Tree tree;

        private final boolean targetPrediction;

        private final Head head;

        public TreeParser(final RandomTree randomTree, final Object tree, final Attribute labelAttribute,
                          final boolean targetPrediction) {
            this.info = ReflectionUtil.getDeclaredField(randomTree, "m_Info");
            this.tree = (Tree) tree;
            this.targetPrediction = targetPrediction;
            this.head = new Head(new NominalCondition(labelAttribute, targetPrediction ? "1" : "0"));
        }

        public RuleSet getRules() {
            RuleSet rules = new RuleSet();
            descend(rules, tree, null);
            return rules;
        }

        private void descend(final RuleSet rules, final Tree tree, final Body body) {
            int attributeIndex = ReflectionUtil.getDeclaredField(tree, "m_Attribute");
            Tree[] successors = ReflectionUtil.getDeclaredField(tree, "m_Successors");

            if (attributeIndex >= 0) {
                Attribute attribute = info.attribute(attributeIndex);

                for (int i = 0; i < successors.length; i++) {
                    Condition condition = null;

                    if (attribute.isNumeric()) {
                        double splitPoint = ReflectionUtil.getDeclaredField(tree, "m_SplitPoint");
                        NumericCondition.Comparator comparator = i > 0 ?
                                NumericCondition.Comparator.GREATER_OR_EQUAL : NumericCondition.Comparator.LESS;
                        condition = new NumericCondition(attribute, splitPoint, comparator);
                    } else {
                        condition = new NominalCondition(attribute, attribute.value(i));
                    }

                    Body newBody = body != null ? new Body(body) : new Body();
                    newBody.addCondition(condition);
                    Tree successor = successors[i];
                    descend(rules, successor, newBody);
                }
            } else if (body != null && (successors == null || successors.length == 0)) {
                double[] classDistribution = ReflectionUtil.getDeclaredField(tree, "m_ClassDistribution");

                if (classDistribution != null) {
                    boolean prediction = classDistribution[1] > classDistribution[0];

                    if (prediction == targetPrediction) {
                        Rule rule = new Rule(body, new Head(head));
                        rules.add(rule);
                        LOG.trace("{}", rule);
                    }
                }
            }
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(RandomForestRuleGenerationLearner.class);

    private static final int NUM_ITERATIONS = 10;

    public RandomForestRuleGenerationLearner(final String name, final RuleGenerationConfiguration configuration) {
        this(name, configuration, new MultiplePredictionStats());
    }

    public RandomForestRuleGenerationLearner(final String name, final RuleGenerationConfiguration configuration,
                                             final MultiplePredictionStats predictionStats) {
        super(name, configuration, predictionStats);
    }

    @Override
    protected RuleSet buildModel(final DataSet trainingDataSet) throws Exception {
        MultiLabelInstances multiLabelInstances = trainingDataSet.getDataSet();
        RuleSet ruleSet = new RuleSet();
        int minRules = getConfiguration().getMinRules();
        int seed = 1;
        int size;

        while ((size = ruleSet.size()) < minRules) {
            LOG.info("Generating more rules. {} of {} rules generated so far...", size, minRules);
            RuleSet newRules = generateRules(trainingDataSet, multiLabelInstances, seed);
            ruleSet.addAll(newRules);

            if (Math.abs(ruleSet.size() - size) <= 10) {
                LOG.info("Unable to generate more rules...");
                break;
            }

            seed++;
        }

        LOG.info("{} rules generated", ruleSet.size());
        return ruleSet;
    }

    private RuleSet generateRules(final DataSet trainingDataSet, final MultiLabelInstances multiLabelInstances,
                                  final int seed)
            throws Exception {
        RuleSet ruleSet = new RuleSet();

        for (int maxDepth = 0; maxDepth <= 5; maxDepth++) {
            LOG.trace("Building {} random trees with max depth of {}...", NUM_ITERATIONS, maxDepth);
            RandomForest learner = new RandomForest();
            learner.setNumExecutionSlots(Runtime.getRuntime().availableProcessors());
            learner.setMaxDepth(maxDepth);
            learner.setNumIterations(NUM_ITERATIONS);
            learner.setSeed(seed);
            BinaryRelevance model = new BinaryRelevance(learner);
            model.build(multiLabelInstances);

            int[] labelIndices = trainingDataSet.getDataSet().getLabelIndices();
            String[] labelNames = trainingDataSet.getDataSet().getLabelNames();

            for (int i = 0; i < labelIndices.length; i++) {
                int labelIndex = labelIndices[i];
                String labelName = labelNames[i];
                boolean targetPrediction = trainingDataSet.getTargetPrediction(labelIndex);
                Classifier classifier = model.getModel(labelName);
                RandomForest randomForest = (RandomForest) classifier;
                Classifier[] trees = ReflectionUtil.getInheritedField(randomForest, "m_Classifiers");

                for (Classifier tree : trees) {
                    Attribute labelAttribute = trainingDataSet.getDataSet().getDataSet().attribute(labelIndex);
                    RandomTree randomTree = (RandomTree) tree;
                    TreeParser treeParser = new TreeParser(randomTree,
                            ReflectionUtil.getDeclaredField(randomTree, "m_Tree"), labelAttribute, targetPrediction);
                    ruleSet.addAll(treeParser.getRules());
                }
            }
        }

        return ruleSet;
    }

    @Override
    protected AbstractMultiLabelRuleLearner<RuleGenerationConfiguration> copy() {
        return new RandomForestRuleGenerationLearner(getName(), getConfiguration(), getPredictionStats());
    }

    @Override
    public TechnicalInformation getTechnicalInformation() {
        throw new UnsupportedOperationException();
    }

}
