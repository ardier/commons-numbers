/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.numbers.fraction;

import org.apache.commons.numbers.core.TestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 */
public class FractionTest {

    private void assertFraction(int expectedNumerator, int expectedDenominator, Fraction actual) {
        Assert.assertEquals(expectedNumerator, actual.getNumerator());
        Assert.assertEquals(expectedDenominator, actual.getDenominator());
    }

    @Test
    public void testConstructor() {
        assertFraction(0, 1, Fraction.of(0, 1));
        assertFraction(0, 1, Fraction.of(0, 2));
        assertFraction(0, 1, Fraction.of(0, -1));
        assertFraction(1, 2, Fraction.of(1, 2));
        assertFraction(1, 2, Fraction.of(2, 4));
        assertFraction(-1, 2, Fraction.of(-1, 2));
        assertFraction(-1, 2, Fraction.of(1, -2));
        assertFraction(-1, 2, Fraction.of(-2, 4));
        assertFraction(-1, 2, Fraction.of(2, -4));

        // overflow
        try {
            Fraction.of(Integer.MIN_VALUE, -1);
            Assert.fail();
        } catch (ArithmeticException ex) {
            // success
        }
        try {
            Fraction.of(1, Integer.MIN_VALUE);
            Assert.fail();
        } catch (ArithmeticException ex) {
            // success
        }

        assertFraction(0, 1, Fraction.from(0.00000000000001));
        assertFraction(2, 5, Fraction.from(0.40000000000001));
        assertFraction(15, 1, Fraction.from(15.0000000000001));
    }

    @Test
    public void testGoldenRatio() {
        // the golden ratio is notoriously a difficult number for continuous fraction
        Assertions.assertThrows(ArithmeticException.class,
                () -> Fraction.from((1 + Math.sqrt(5)) / 2, 1.0e-12, 25)
        );
    }

    // MATH-179
    @Test
    public void testDoubleConstructor() throws Exception  {
        assertFraction(1, 2, Fraction.from((double)1 / (double)2));
        assertFraction(1, 3, Fraction.from((double)1 / (double)3));
        assertFraction(2, 3, Fraction.from((double)2 / (double)3));
        assertFraction(1, 4, Fraction.from((double)1 / (double)4));
        assertFraction(3, 4, Fraction.from((double)3 / (double)4));
        assertFraction(1, 5, Fraction.from((double)1 / (double)5));
        assertFraction(2, 5, Fraction.from((double)2 / (double)5));
        assertFraction(3, 5, Fraction.from((double)3 / (double)5));
        assertFraction(4, 5, Fraction.from((double)4 / (double)5));
        assertFraction(1, 6, Fraction.from((double)1 / (double)6));
        assertFraction(5, 6, Fraction.from((double)5 / (double)6));
        assertFraction(1, 7, Fraction.from((double)1 / (double)7));
        assertFraction(2, 7, Fraction.from((double)2 / (double)7));
        assertFraction(3, 7, Fraction.from((double)3 / (double)7));
        assertFraction(4, 7, Fraction.from((double)4 / (double)7));
        assertFraction(5, 7, Fraction.from((double)5 / (double)7));
        assertFraction(6, 7, Fraction.from((double)6 / (double)7));
        assertFraction(1, 8, Fraction.from((double)1 / (double)8));
        assertFraction(3, 8, Fraction.from((double)3 / (double)8));
        assertFraction(5, 8, Fraction.from((double)5 / (double)8));
        assertFraction(7, 8, Fraction.from((double)7 / (double)8));
        assertFraction(1, 9, Fraction.from((double)1 / (double)9));
        assertFraction(2, 9, Fraction.from((double)2 / (double)9));
        assertFraction(4, 9, Fraction.from((double)4 / (double)9));
        assertFraction(5, 9, Fraction.from((double)5 / (double)9));
        assertFraction(7, 9, Fraction.from((double)7 / (double)9));
        assertFraction(8, 9, Fraction.from((double)8 / (double)9));
        assertFraction(1, 10, Fraction.from((double)1 / (double)10));
        assertFraction(3, 10, Fraction.from((double)3 / (double)10));
        assertFraction(7, 10, Fraction.from((double)7 / (double)10));
        assertFraction(9, 10, Fraction.from((double)9 / (double)10));
        assertFraction(1, 11, Fraction.from((double)1 / (double)11));
        assertFraction(2, 11, Fraction.from((double)2 / (double)11));
        assertFraction(3, 11, Fraction.from((double)3 / (double)11));
        assertFraction(4, 11, Fraction.from((double)4 / (double)11));
        assertFraction(5, 11, Fraction.from((double)5 / (double)11));
        assertFraction(6, 11, Fraction.from((double)6 / (double)11));
        assertFraction(7, 11, Fraction.from((double)7 / (double)11));
        assertFraction(8, 11, Fraction.from((double)8 / (double)11));
        assertFraction(9, 11, Fraction.from((double)9 / (double)11));
        assertFraction(10, 11, Fraction.from((double)10 / (double)11));
    }

    // MATH-181
    @Test
    public void testDigitLimitConstructor() throws Exception  {
        assertFraction(2, 5, Fraction.from(0.4,   9));
        assertFraction(2, 5, Fraction.from(0.4,  99));
        assertFraction(2, 5, Fraction.from(0.4, 999));

        assertFraction(3, 5,      Fraction.from(0.6152,    9));
        assertFraction(8, 13,     Fraction.from(0.6152,   99));
        assertFraction(510, 829,  Fraction.from(0.6152,  999));
        assertFraction(769, 1250, Fraction.from(0.6152, 9999));

        // MATH-996
        assertFraction(1, 2, Fraction.from(0.5000000001, 10));
    }

    @Test
    public void testIntegerOverflow() {
        checkIntegerOverflow(0.75000000001455192);
        checkIntegerOverflow(1.0e10);
        checkIntegerOverflow(-1.0e10);
        checkIntegerOverflow(-43979.60679604749);
    }

    private void checkIntegerOverflow(double a) {
        try {
            @SuppressWarnings("unused")
            Fraction f = Fraction.from(a, 1.0e-12, 1000);
            //System.out.println(f.getNumerator() + "/" + f.getDenominator());
            Assert.fail("an exception should have been thrown");
        } catch (ArithmeticException ignored) {
            // expected behavior
        }
    }

    @Test
    public void testEpsilonLimitConstructor() throws Exception  {
        assertFraction(2, 5, Fraction.from(0.4, 1.0e-5, 100));

        assertFraction(3, 5,      Fraction.from(0.6152, 0.02, 100));
        assertFraction(8, 13,     Fraction.from(0.6152, 1.0e-3, 100));
        assertFraction(251, 408,  Fraction.from(0.6152, 1.0e-4, 100));
        assertFraction(251, 408,  Fraction.from(0.6152, 1.0e-5, 100));
        assertFraction(510, 829,  Fraction.from(0.6152, 1.0e-6, 100));
        assertFraction(769, 1250, Fraction.from(0.6152, 1.0e-7, 100));
    }

    @Test
    public void testCompareTo() {
        Fraction first = Fraction.of(1, 2);
        Fraction second = Fraction.of(1, 3);
        Fraction third = Fraction.of(1, 2);

        Assert.assertEquals(0, first.compareTo(first));
        Assert.assertEquals(0, first.compareTo(third));
        Assert.assertEquals(1, first.compareTo(second));
        Assert.assertEquals(-1, second.compareTo(first));

        // these two values are different approximations of PI
        // the first  one is approximately PI - 3.07e-18
        // the second one is approximately PI + 1.936e-17
        Fraction pi1 = Fraction.of(1068966896, 340262731);
        Fraction pi2 = Fraction.of( 411557987, 131002976);
        Assert.assertEquals(-1, pi1.compareTo(pi2));
        Assert.assertEquals( 1, pi2.compareTo(pi1));
        Assert.assertEquals(0.0, pi1.doubleValue() - pi2.doubleValue(), 1.0e-20);
    }

    @Test
    public void testDoubleValue() {
        Fraction first = Fraction.of(1, 2);
        Fraction second = Fraction.of(1, 3);

        Assert.assertEquals(0.5, first.doubleValue(), 0.0);
        Assert.assertEquals(1.0 / 3.0, second.doubleValue(), 0.0);
    }

    @Test
    public void testFloatValue() {
        Fraction first = Fraction.of(1, 2);
        Fraction second = Fraction.of(1, 3);

        Assert.assertEquals(0.5f, first.floatValue(), 0.0f);
        Assert.assertEquals((float)(1.0 / 3.0), second.floatValue(), 0.0f);
    }

    @Test
    public void testIntValue() {
        Fraction first = Fraction.of(1, 2);
        Fraction second = Fraction.of(3, 2);

        Assert.assertEquals(0, first.intValue());
        Assert.assertEquals(1, second.intValue());
    }

    @Test
    public void testLongValue() {
        Fraction first = Fraction.of(1, 2);
        Fraction second = Fraction.of(3, 2);

        Assert.assertEquals(0L, first.longValue());
        Assert.assertEquals(1L, second.longValue());
    }

    @Test
    public void testConstructorDouble() {
        assertFraction(1, 2, Fraction.from(0.5));
        assertFraction(1, 3, Fraction.from(1.0 / 3.0));
        assertFraction(17, 100, Fraction.from(17.0 / 100.0));
        assertFraction(317, 100, Fraction.from(317.0 / 100.0));
        assertFraction(-1, 2, Fraction.from(-0.5));
        assertFraction(-1, 3, Fraction.from(-1.0 / 3.0));
        assertFraction(-17, 100, Fraction.from(17.0 / -100.0));
        assertFraction(-317, 100, Fraction.from(-317.0 / 100.0));
    }

    @Test
    public void testAbs() {
        Fraction a = Fraction.of(10, 21);
        Fraction b = Fraction.of(-10, 21);
        Fraction c = Fraction.of(10, -21);

        assertFraction(10, 21, a.abs());
        assertFraction(10, 21, b.abs());
        assertFraction(10, 21, c.abs());
    }

    @Test
    public void testMath1261() {
        final Fraction a = Fraction.of(Integer.MAX_VALUE, 2);
        final Fraction b = a.multiply(2);
        Assert.assertEquals(b, Fraction.of(Integer.MAX_VALUE));

        final Fraction c = Fraction.of(2, Integer.MAX_VALUE);
        final Fraction d = c.divide(2);
        Assert.assertEquals(d, Fraction.of(1, Integer.MAX_VALUE));
    }

    @Test
    public void testReciprocal() {
        Fraction f = null;

        f = Fraction.of(50, 75);
        f = f.reciprocal();
        Assert.assertEquals(3, f.getNumerator());
        Assert.assertEquals(2, f.getDenominator());

        f = Fraction.of(4, 3);
        f = f.reciprocal();
        Assert.assertEquals(3, f.getNumerator());
        Assert.assertEquals(4, f.getDenominator());

        f = Fraction.of(-15, 47);
        f = f.reciprocal();
        Assert.assertEquals(-47, f.getNumerator());
        Assert.assertEquals(15, f.getDenominator());

        f = Fraction.of(0, 3);
        try {
            f = f.reciprocal();
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ignored) {}

        // large values
        f = Fraction.of(Integer.MAX_VALUE, 1);
        f = f.reciprocal();
        Assert.assertEquals(1, f.getNumerator());
        Assert.assertEquals(Integer.MAX_VALUE, f.getDenominator());
    }

    @Test
    public void testNegate() {
        Fraction f = null;

        f = Fraction.of(50, 75);
        f = f.negate();
        Assert.assertEquals(-2, f.getNumerator());
        Assert.assertEquals(3, f.getDenominator());

        f = Fraction.of(-50, 75);
        f = f.negate();
        Assert.assertEquals(2, f.getNumerator());
        Assert.assertEquals(3, f.getDenominator());

        // large values
        f = Fraction.of(Integer.MAX_VALUE-1, Integer.MAX_VALUE);
        f = f.negate();
        Assert.assertEquals(Integer.MIN_VALUE+2, f.getNumerator());
        Assert.assertEquals(Integer.MAX_VALUE, f.getDenominator());

        f = Fraction.of(Integer.MIN_VALUE, 1);
        try {
            f = f.negate();
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}
    }

    @Test
    public void testAdd() {
        Fraction a = Fraction.of(1, 2);
        Fraction b = Fraction.of(2, 3);

        assertFraction(1, 1, a.add(a));
        assertFraction(7, 6, a.add(b));
        assertFraction(7, 6, b.add(a));
        assertFraction(4, 3, b.add(b));

        Fraction f1 = Fraction.of(Integer.MAX_VALUE - 1, 1);
        Fraction f2 = Fraction.ONE;
        Fraction f = f1.add(f2);
        Assert.assertEquals(Integer.MAX_VALUE, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());
        f = f1.add(1);
        Assert.assertEquals(Integer.MAX_VALUE, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());

        f1 = Fraction.of(-1, 13*13*2*2);
        f2 = Fraction.of(-2, 13*17*2);
        f = f1.add(f2);
        Assert.assertEquals(13*13*17*2*2, f.getDenominator());
        Assert.assertEquals(-17 - 2*13*2, f.getNumerator());

        try {
            f.add(null);
            Assert.fail("expecting NullArgumentException");
        } catch (NullPointerException ex) {}

        // if this fraction is added naively, it will overflow.
        // check that it doesn't.
        f1 = Fraction.of(1,32768*3);
        f2 = Fraction.of(1,59049);
        f = f1.add(f2);
        Assert.assertEquals(52451, f.getNumerator());
        Assert.assertEquals(1934917632, f.getDenominator());

        f1 = Fraction.of(Integer.MIN_VALUE, 3);
        f2 = Fraction.of(1,3);
        f = f1.add(f2);
        Assert.assertEquals(Integer.MIN_VALUE+1, f.getNumerator());
        Assert.assertEquals(3, f.getDenominator());

        f1 = Fraction.of(Integer.MAX_VALUE - 1, 1);
        f2 = Fraction.ONE;
        f = f1.add(f2);
        Assert.assertEquals(Integer.MAX_VALUE, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());

        try {
            f = f.add(Fraction.ONE); // should overflow
            Assert.fail("expecting ArithmeticException but got: " + f.toString());
        } catch (ArithmeticException ex) {}

        // denominator should not be a multiple of 2 or 3 to trigger overflow
        f1 = Fraction.of(Integer.MIN_VALUE, 5);
        f2 = Fraction.of(-1,5);
        try {
            f = f1.add(f2); // should overflow
            Assert.fail("expecting ArithmeticException but got: " + f.toString());
        } catch (ArithmeticException ex) {}

        try {
            f= Fraction.of(-Integer.MAX_VALUE, 1);
            f = f.add(f);
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}

        try {
            f= Fraction.of(-Integer.MAX_VALUE, 1);
            f = f.add(f);
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}

        f1 = Fraction.of(3,327680);
        f2 = Fraction.of(2,59049);
        try {
            f = f1.add(f2); // should overflow
            Assert.fail("expecting ArithmeticException but got: " + f.toString());
        } catch (ArithmeticException ex) {}
    }

    @Test
    public void testDivide() {
        Fraction a = Fraction.of(1, 2);
        Fraction b = Fraction.of(2, 3);

        assertFraction(1, 1, a.divide(a));
        assertFraction(3, 4, a.divide(b));
        assertFraction(4, 3, b.divide(a));
        assertFraction(1, 1, b.divide(b));

        Fraction f1 = Fraction.of(3, 5);
        Fraction f2 = Fraction.ZERO;
        try {
            f1.divide(f2);
            Assert.fail("expecting FractionException");
        } catch (FractionException ex) {}

        f1 = Fraction.of(0, 5);
        f2 = Fraction.of(2, 7);
        Fraction f = f1.divide(f2);
        Assert.assertSame(Fraction.ZERO, f);

        f1 = Fraction.of(2, 7);
        f2 = Fraction.ONE;
        f = f1.divide(f2);
        Assert.assertEquals(2, f.getNumerator());
        Assert.assertEquals(7, f.getDenominator());

        f1 = Fraction.of(1, Integer.MAX_VALUE);
        f = f1.divide(f1);
        Assert.assertEquals(1, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());

        f1 = Fraction.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
        f2 = Fraction.of(1, Integer.MAX_VALUE);
        f = f1.divide(f2);
        Assert.assertEquals(Integer.MIN_VALUE, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());

        try {
            f.divide(null);
            Assert.fail("NullArgumentException");
        } catch (NullPointerException ex) {}

        try {
            f1 = Fraction.of(1, Integer.MAX_VALUE);
            f = f1.divide(f1.reciprocal());  // should overflow
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}
        try {
            f1 = Fraction.of(1, -Integer.MAX_VALUE);
            f = f1.divide(f1.reciprocal());  // should overflow
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}

        f1 = Fraction.of(6, 35);
        f  = f1.divide(15);
        Assert.assertEquals(2, f.getNumerator());
        Assert.assertEquals(175, f.getDenominator());

    }

    @Test
    public void testMultiply() {
        Fraction a = Fraction.of(1, 2);
        Fraction b = Fraction.of(2, 3);

        assertFraction(1, 4, a.multiply(a));
        assertFraction(1, 3, a.multiply(b));
        assertFraction(1, 3, b.multiply(a));
        assertFraction(4, 9, b.multiply(b));

        Fraction f1 = Fraction.of(Integer.MAX_VALUE, 1);
        Fraction f2 = Fraction.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Fraction f = f1.multiply(f2);
        Assert.assertEquals(Integer.MIN_VALUE, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());

        try {
            f.multiply(null);
            Assert.fail("expecting NullArgumentException");
        } catch (NullPointerException ex) {}

        f1 = Fraction.of(6, 35);
        f  = f1.multiply(15);
        Assert.assertEquals(18, f.getNumerator());
        Assert.assertEquals(7, f.getDenominator());
    }

    @Test
    public void testPow() {
        Fraction a = Fraction.of(3, 7);
        assertFraction(1, 1, a.pow(0));
        assertFraction(3, 7, a.pow(1));
        assertFraction(7, 3, a.pow(-1));
        assertFraction(9, 49, a.pow(2));
        assertFraction(49, 9, a.pow(-2));

        Fraction b = Fraction.of(3, -7);
        assertFraction(1, 1, b.pow(0));
        assertFraction(-3, 7, b.pow(1));
        assertFraction(-7, 3, b.pow(-1));
        assertFraction(9, 49, a.pow(2));
        assertFraction(49, 9, a.pow(-2));

        Fraction c = Fraction.of(0, -11);
        assertFraction(0, 1, c.pow(Integer.MAX_VALUE));
    }

    @Test
    public void testSubtract() {
        Fraction a = Fraction.of(1, 2);
        Fraction b = Fraction.of(2, 3);

        assertFraction(0, 1, a.subtract(a));
        assertFraction(-1, 6, a.subtract(b));
        assertFraction(1, 6, b.subtract(a));
        assertFraction(0, 1, b.subtract(b));

        Fraction f = Fraction.of(1,1);
        try {
            f.subtract(null);
            Assert.fail("expecting NullArgumentException");
        } catch (NullPointerException ex) {}

        // if this fraction is subtracted naively, it will overflow.
        // check that it doesn't.
        Fraction f1 = Fraction.of(1,32768*3);
        Fraction f2 = Fraction.of(1,59049);
        f = f1.subtract(f2);
        Assert.assertEquals(-13085, f.getNumerator());
        Assert.assertEquals(1934917632, f.getDenominator());

        f1 = Fraction.of(Integer.MIN_VALUE, 3);
        f2 = Fraction.of(1,3).negate();
        f = f1.subtract(f2);
        Assert.assertEquals(Integer.MIN_VALUE+1, f.getNumerator());
        Assert.assertEquals(3, f.getDenominator());

        f1 = Fraction.of(Integer.MAX_VALUE, 1);
        f2 = Fraction.ONE;
        f = f1.subtract(f2);
        Assert.assertEquals(Integer.MAX_VALUE-1, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());
        f = f1.subtract(1);
        Assert.assertEquals(Integer.MAX_VALUE-1, f.getNumerator());
        Assert.assertEquals(1, f.getDenominator());

        try {
            f1 = Fraction.of(1, Integer.MAX_VALUE);
            f2 = Fraction.of(1, Integer.MAX_VALUE - 1);
            f = f1.subtract(f2);
            Assert.fail("expecting ArithmeticException");  //should overflow
        } catch (ArithmeticException ex) {}

        // denominator should not be a multiple of 2 or 3 to trigger overflow
        f1 = Fraction.of(Integer.MIN_VALUE, 5);
        f2 = Fraction.of(1,5);
        try {
            f = f1.subtract(f2); // should overflow
            Assert.fail("expecting ArithmeticException but got: " + f.toString());
        } catch (ArithmeticException ex) {}

        try {
            f= Fraction.of(Integer.MIN_VALUE, 1);
            f = f.subtract(Fraction.ONE);
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}

        try {
            f= Fraction.of(Integer.MAX_VALUE, 1);
            f = f.subtract(Fraction.ONE.negate());
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}

        f1 = Fraction.of(3,327680);
        f2 = Fraction.of(2,59049);
        try {
            f = f1.subtract(f2); // should overflow
            Assert.fail("expecting ArithmeticException but got: " + f.toString());
        } catch (ArithmeticException ex) {}
    }

    @Test
    public void testEqualsAndHashCode() {
        Fraction zero  = Fraction.of(0,1);
        Fraction nullFraction = null;
        Assert.assertEquals(zero, zero);
        Assert.assertNotEquals(zero, nullFraction);
        Assert.assertNotEquals(zero, Double.valueOf(0), 0.0);
        Fraction zero2 = Fraction.of(0,2);
        Assert.assertEquals(zero, zero2);
        Assert.assertEquals(zero.hashCode(), zero2.hashCode());
        Fraction one = Fraction.of(1,1);
        Assert.assertFalse((one.equals(zero) ||zero.equals(one)));
    }

    @Test
    public void testGetReducedFraction() {
        Fraction threeFourths = Fraction.of(3, 4);
        Assert.assertEquals(threeFourths, Fraction.getReducedFraction(6, 8));
        Assert.assertEquals(Fraction.ZERO, Fraction.getReducedFraction(0, -1));
        try {
            Fraction.getReducedFraction(1, 0);
            Assert.fail("expecting ArithmeticException");
        } catch (ArithmeticException ignored) {
            // expected
        }
        Assert.assertEquals(-1, Fraction.getReducedFraction
                (2, Integer.MIN_VALUE).getNumerator());
        Assert.assertEquals(-1, Fraction.getReducedFraction
                (1, -1).getNumerator());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("0", Fraction.of(0, 3).toString());
        Assert.assertEquals("3", Fraction.of(6, 2).toString());
        Assert.assertEquals("2 / 3", Fraction.of(18, 27).toString());
        Assert.assertEquals("-10 / 11", Fraction.of(-10, 11).toString());
        Assert.assertEquals("-10 / 11", Fraction.of(10, -11).toString());
    }

    @Test
    public void testSerial() {
        Fraction[] fractions = {
            Fraction.of(3, 4), Fraction.ONE, Fraction.ZERO,
            Fraction.of(17), Fraction.from(Math.PI, 1000),
            Fraction.of(-5, 2)
        };
        for (Fraction fraction : fractions) {
            Assert.assertEquals(fraction, TestUtils.serializeAndRecover(fraction));
        }
    }

    @Test
    public void testParse() {
        String[] validExpressions = new String[] {
                "1 / 2", "15 / 16", "-2 / 3", "8 / 7"
        };
        Fraction[] fractions = {
                Fraction.of(1, 2),
                Fraction.of(15, 16),
                Fraction.of(-2, 3),
                Fraction.of(8, 7),
        };
        int inc = 0;
        for (Fraction fraction: fractions) {
            Assert.assertEquals(fraction,
                    Fraction.parse(validExpressions[inc]));
            inc++;
        }
    }
}
