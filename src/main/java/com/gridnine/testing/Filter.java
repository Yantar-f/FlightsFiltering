package com.gridnine.testing;

import java.util.ArrayList;
import java.util.List;

public interface Filter<T> {
    List<T> apply(List<T> tList);

    static <T> FilterBuilder<T> builder(Condition<T> condition) {
        return new FilterBuilder<T>(condition);
    }

    class FilterBuilder<T> {
        List<ConjunctionCondition<T>> conditionsDisjunction;

        public FilterBuilder(Condition<T> condition) {
            conditionsDisjunction = new ArrayList<>(5);
            conditionsDisjunction.add(new ConjunctionCondition<>(condition));
        }
        
        public Filter<T> build() {
            return new Filter<>() {
                private final List<Condition<T>> conditions =
                        new ArrayList<>(FilterBuilder.this.conditionsDisjunction);

                @Override
                public List<T> apply(List<T> tList) {
                    return tList.stream()
                            .filter(t -> conditions.stream().anyMatch(c -> c.isApplied(t)))
                            .toList();
                }
            };
        }
        
        public FilterBuilder<T> or(Condition<T> condition) {
            conditionsDisjunction.add(new ConjunctionCondition<>(condition));
            return this;
        }
        
        public FilterBuilder<T> and(Condition<T> condition) {
            conditionsDisjunction.get(conditionsDisjunction.size()-1).add(condition);
            return this;
        }
    }

    class ConjunctionCondition<T> implements Condition<T> {
        private final List<Condition<T>> conditionsConjunction;

        public ConjunctionCondition(Condition<T> condition) {
            conditionsConjunction = new ArrayList<>(5);
            conditionsConjunction.add(condition);
        }

        @Override
        public boolean isApplied(T t) {
            return conditionsConjunction.stream().allMatch(condition -> condition.isApplied(t));
        }

        public void add(Condition<T> condition) {
            conditionsConjunction.add(condition);
        }
    }
}
