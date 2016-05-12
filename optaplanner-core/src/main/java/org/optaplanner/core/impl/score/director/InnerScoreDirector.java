/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.director;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface InnerScoreDirector<Solution_> extends ScoreDirector<Solution_> {

    /**
     * @return used to check {@link #isWorkingEntityListDirty(long)} later on
     */
    long getWorkingEntityListRevision();

    /**
     * @param expectedWorkingEntityListRevision an
     * @return true if the entityList might have a different set of instances now
     */
    boolean isWorkingEntityListDirty(long expectedWorkingEntityListRevision);

    /**
     * @return never null
     */
    InnerScoreDirectorFactory<Solution_> getScoreDirectorFactory();

    /**
     * @return never null
     */
    SolutionDescriptor<Solution_> getSolutionDescriptor();

    /**
     * @return never null
     */
    ScoreDefinition getScoreDefinition();

    /**
     * @return never null
     */
    Solution_ cloneWorkingSolution();

    /**
     * @param originalSolution never null
     * @return never null
     */
    Solution_ cloneSolution(Solution_ originalSolution);

    /**
     * @return {@code >= 0}
     */
    int getWorkingEntityCount();

    /**
     * @return never null: an empty list if there are none
     */
    List<Object> getWorkingEntityList();

    /**
     * @return {@code >= 0}
     */
    int getWorkingValueCount();

    int countWorkingSolutionUninitializedVariables();

    /**
     * @return at least 0L
     */
    long getCalculationCount();

    void resetCalculationCount();

    /**
     * @return never null
     */
    SupplyManager getSupplyManager();

    /**
     * Clones this {@link ScoreDirector} and its {@link PlanningSolution working solution}.
     * Use {@link #getWorkingSolution()} to retrieve the {@link PlanningSolution working solution} of that clone.
     * <p>
     * This is heavy method, because it usually breaks incremental score calculation. Use it sparingly.
     * Therefore it's best to clone lazily by delaying the clone call as long as possible.
     * @return never null
     */
    ScoreDirector<Solution_> clone();

    /**
     * Do not waste performance by propagating changes to step (or higher) mechanisms.
     * @param allChangesWillBeUndoneBeforeStepEnds true if all changes will be undone
     */
    void setAllChangesWillBeUndoneBeforeStepEnds(boolean allChangesWillBeUndoneBeforeStepEnds);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link PlanningSolution working solution}
     * in the current {@link ScoreDirector} (with possibly incremental calculation residue),
     * it is equal to the parameter {@link Score expectedWorkingScore}.
     * <p>
     * Used to assert that skipping {@link #calculateScore()} (when the score is otherwise determined) is correct.
     * @param expectedWorkingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     * is included in the exception message
     */
    void assertExpectedWorkingScore(Score expectedWorkingScore, Object completedAction);

    /**
     * Asserts that if all {@link VariableListener}s are forcibly triggered,
     * and therefore all shadow variables are updated if needed,
     * that none of the shadow variables of the {@link PlanningSolution working solution} change,
     * Then also asserts that the {@link Score} calculated for the {@link PlanningSolution working solution} afterwards
     * is equal to the parameter {@link Score expectedWorkingScore}.
     * <p>
     * Used to assert that the shadow variables' state is consistent with the genuine variables' state.
     * @param expectedWorkingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     * is included in the exception message
     */
    void assertShadowVariablesAreNotStale(Score expectedWorkingScore, Object completedAction);

    /**
     * Asserts that if the {@link Score} is calculated for the current {@link PlanningSolution working solution}
     * in a fresh {@link ScoreDirector} (with no incremental calculation residue),
     * it is equal to the parameter {@link Score workingScore}.
     * <p>
     * Furthermore, if the assert fails, a score corruption analysis might be included in the exception message.
     * @param workingScore never null
     * @param completedAction sometimes null, when assertion fails then the completedAction's {@link Object#toString()}
     * is included in the exception message
     * @see InnerScoreDirectorFactory#assertScoreFromScratch(Solution_)
     */
    void assertWorkingScoreFromScratch(Score workingScore, Object completedAction);

}
