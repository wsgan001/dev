/**
 * Copyright (c) 2000-2006, Serhiy Yevtushenko
 * All rights reserved.
 * Please read license.txt for licensing issues.
 **/



package conexp.core.calculationstrategies.tests;

import conexp.core.Context;
import conexp.core.ImplicationCalcStrategy;
import conexp.core.calculationstrategies.NextClosedSetImplicationCalculator;

public class NextClosedSetImplicationCalculatorTest extends ImplicationCalculatorTest {

    protected ImplicationCalcStrategy makePreparedCalculator(Context cxt) {
        ImplicationCalcStrategy calc = new NextClosedSetImplicationCalculator();
        calc.setRelation(cxt.getRelation());
        return calc;
    }

}
