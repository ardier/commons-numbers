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
package org.apache.commons.numbers.rootfinder;

import java.util.function.DoubleUnaryOperator;
import org.apache.commons.numbers.core.Precision;

/**
 * This class implements the <a href="http://mathworld.wolfram.com/BrentsMethod.html">
 * Brent algorithm</a> for finding zeros of real univariate functions.
 * The function should be continuous but not necessarily smooth.
 * The {@code solve} method returns a zero {@code x} of the function {@code f}
 * in the given interval {@code [a, b]} to within a tolerance
 * {@code 2 eps abs(x) + t} where {@code eps} is the relative accuracy and
 * {@code t} is the absolute accuracy.
 * <p>The given interval must bracket the root.</p>
 * <p>
 *  The reference implementation is given in chapter 4 of
 *  <blockquote>
 *   <b>Algorithms for Minimization Without Derivatives</b>,
 *   <em>Richard P. Brent</em>,
 *   Dover, 2002
 *  </blockquote>
 */
public class BrentSolver {
    /** Relative accuracy. */
    private final double relativeAccuracy;
    /** Absolute accuracy. */
    private final double absoluteAccuracy;
    /** Function accuracy. */
    private final double functionValueAccuracy;

    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     * @param functionValueAccuracy Function value accuracy.
     */
    public BrentSolver(double relativeAccuracy,
                       double absoluteAccuracy,
                       double functionValueAccuracy) {
        this.relativeAccuracy = relativeAccuracy;
        this.absoluteAccuracy = absoluteAccuracy;
        this.functionValueAccuracy = functionValueAccuracy;
    }

    /**
     * Search the function's zero within the given interval.
     *
     * @param func Function to solve.
     * @param min Lower bound.
     * @param max Upper bound.
     * @return the root.
     * @throws IllegalArgumentException if {@code min > max}.
     * @throws IllegalArgumentException if the given interval does
     * not bracket the root.
     */
    public double findRoot(DoubleUnaryOperator func,
                           double min,
                           double max) {
        return findRoot(func, min, 0.5 * (min + max), max);
    }

    /**
     * Search the function's zero within the given interval,
     * starting from the given estimate.
     *
     * @param func Function to solve.
     * @param min Lower bound.
     * @param initial Initial guess.
     * @param max Upper bound.
     * @return the root.
     * @throws IllegalArgumentException if {@code min > max} or
     * {@code initial} is not in the {@code [min, max]} interval.
     * @throws IllegalArgumentException if the given interval does
     * not bracket the root.
     */
    public double findRoot(DoubleUnaryOperator func,
                           double min,
                           double initial,
                           double max) {
        if (min > max) {
            throw new SolverException(SolverException.TOO_LARGE, min, max);
        }
        if (initial < min ||
            initial > max) {
            throw new SolverException(SolverException.OUT_OF_RANGE, initial, min, max);
        }

        // Return the initial guess if it is good enough.
        final double yInitial = func.applyAsDouble(initial);
        if (Math.abs(yInitial) <= functionValueAccuracy) {
            return initial;
        }

        // Return the first endpoint if it is good enough.
        final double yMin = func.applyAsDouble(min);
        if (Math.abs(yMin) <= functionValueAccuracy) {
            return min;
        }

        // Reduce interval if min and initial bracket the root.
        if (Double.compare(yInitial * yMin, 0.0) < 0) {
            return brent(func, min, initial, yMin, yInitial);
        }

        // Return the second endpoint if it is good enough.
        final double yMax = func.applyAsDouble(max);
        if (Math.abs(yMax) <= functionValueAccuracy) {
            return max;
        }

        // Reduce interval if initial and max bracket the root.
        if (Double.compare(yInitial * yMax, 0.0) < 0) {
            return brent(func, initial, max, yInitial, yMax);
        }

        throw new SolverException(SolverException.BRACKETING, min, yMin, max, yMax);
    }

    /**
     * Search for a zero inside the provided interval.
     * This implementation is based on the algorithm described at page 58 of
     * the book
     * <blockquote>
     *  <b>Algorithms for Minimization Without Derivatives</b>,
     *  <i>Richard P. Brent</i>,
     *  Dover 0-486-41998-3
     * </blockquote>
     *
     * @param func Function to solve.
     * @param lo Lower bound of the search interval.
     * @param hi Higher bound of the search interval.
     * @param fLo Function value at the lower bound of the search interval.
     * @param fHi Function value at the higher bound of the search interval.
     * @return the value where the function is zero.
     */
    private double brent(DoubleUnaryOperator func,
                         double lo, double hi,
                         double fLo, double fHi) {
        double a = lo;
        double fa = fLo;
        double b = hi;
        double fb = fHi;
        double c = a;
        double fc = fa;
        double d = b - a;
        double e = d;

        final double t = absoluteAccuracy;
        final double eps = relativeAccuracy;

        while (true) {
            if (Math.abs(fc) < Math.abs(fb)) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }

            final double tol = 2 * eps * Math.abs(b) + t;
            final double m = 0.5 * (c - b);

            if (Math.abs(m) <= tol ||
                Precision.equals(fb, 0))  {
                return b;
            }
            if (Math.abs(e) < tol ||
                Math.abs(fa) <= Math.abs(fb)) {
                // Force bisection.
                d = m;
                e = d;
            } else {
                double s = fb / fa;
                double p;
                double q;
                // The equality test (a == c) is intentional,
                // it is part of the original Brent's method and
                // it should NOT be replaced by proximity test.
                if (a == c) {
                    // Linear interpolation.
                    p = 2 * m * s;
                    q = 1 - s;
                } else {
                    // Inverse quadratic interpolation.
                    q = fa / fc;
                    final double r = fb / fc;
                    p = s * (2 * m * q * (q - r) - (b - a) * (r - 1));
                    q = (q - 1) * (r - 1) * (s - 1);
                }
                if (p > 0) {
                    q = -q;
                } else {
                    p = -p;
                }
                s = e;
                e = d;
                if (p >= 1.5 * m * q - Math.abs(tol * q) ||
                    p >= Math.abs(0.5 * s * q)) {
                    // Inverse quadratic interpolation gives a value
                    // in the wrong direction, or progress is slow.
                    // Fall back to bisection.
                    d = m;
                    e = d;
                } else {
                    d = p / q;
                }
            }
            a = b;
            fa = fb;

            if (Math.abs(d) > tol) {
                b += d;
            } else if (m > 0) {
                b += tol;
            } else {
                b -= tol;
            }
            fb = func.applyAsDouble(b);
            if ((fb > 0 && fc > 0) ||
                (fb <= 0 && fc <= 0)) {
                c = a;
                fc = fa;
                d = b - a;
                e = d;
            }
        }
    }
}
