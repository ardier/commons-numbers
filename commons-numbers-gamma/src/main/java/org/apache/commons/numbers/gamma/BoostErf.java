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

//  (C) Copyright John Maddock 2006.
//  Use, modification and distribution are subject to the
//  Boost Software License, Version 1.0. (See accompanying file
//  LICENSE_1_0.txt or copy at http://www.boost.org/LICENSE_1_0.txt)

package org.apache.commons.numbers.gamma;

/**
 * Implementation of the <a href="http://mathworld.wolfram.com/Erf.html">error function</a> and
 * its inverse.
 *
 * <p>This code has been adapted from the <a href="https://www.boost.org/">Boost</a>
 * {@code c++} implementation {@code <boost/math/special_functions/erf.hpp>}.
 * All work is copyright John Maddock 2006 and subject to the Boost Software License.
 *
 * @see
 * <a href="https://www.boost.org/doc/libs/1_77_0/libs/math/doc/html/math_toolkit/sf_erf/error_function.html">
 * Boost C++ Error functions</a>
 */
final class BoostErf {
    /**
     * The multiplier used to split the double value into high and low parts. From
     * Dekker (1971): "The constant should be chosen equal to 2^(p - p/2) + 1,
     * where p is the number of binary digits in the mantissa". Here p is 53
     * and the multiplier is {@code 2^27 + 1}.
     */
    private static final double MULTIPLIER = 1.0 + 0x1.0p27;

    /** Private constructor. */
    private BoostErf() {
        // intentionally empty.
    }

    // Code ported from Boost 1.77.0
    //
    // boost/math/special_functions/erf.hpp
    // boost/math/special_functions/detail/erf_inv.hpp
    //
    // Original code comments, including measured deviations, are preserved.
    //
    // Changes to the Boost implementation:
    // - Update method names to replace underscores with camel case
    // - Explicitly inline the polynomial function evaluation
    //   using Horner's method (https://en.wikipedia.org/wiki/Horner%27s_method)
    // - Support odd function for f(0.0) = -f(-0.0)
    // Erf:
    // - Change extended precision z*z to compute the square round-off
    //   using Dekker's method
    // - Change the erf threshold for z when p=1 from
    //   z > 5.8f to z > 5.930664
    // - Change edge case detection for integer z
    // Inverse erf:
    // - Change inverse erf edge case detection to include NaN
    //
    // Note:
    // Constants using the 'f' suffix are machine
    // representable as a float, e.g.
    // assert 0.0891314744949340820313f == 0.0891314744949340820313;
    // The values are unchanged from the Boost reference.

    /**
     * Returns the complementary error function.
     *
     * @param x the value.
     * @return the complementary error function.
     */
    static double erfc(double x) {
        return erfImp(x, true);
    }

    /**
     * Returns the error function.
     *
     * @param x the value.
     * @return the error function.
     */
    static double erf(double x) {
        return erfImp(x, false);
    }

    /**
     * 53-bit implementation for the error function.
     *
     * @param z Point to evaluate
     * @param invert true to invert the result (for the complementary error function)
     * @return the error function result
     */
    private static double erfImp(double z, boolean invert) {
        if (Double.isNaN(z)) {
            return Double.NaN;
        }

        if (z < 0) {
            if (!invert) {
                return -erfImp(-z, invert);
            } else if (z < -0.5) {
                return 2 - erfImp(-z, invert);
            } else {
                return 1 + erfImp(-z, false);
            }
        }

        double result;

        //
        // Big bunch of selection statements now to pick
        // which implementation to use,
        // try to put most likely options first:
        //
        if (z < 0.5) {
            //
            // We're going to calculate erf:
            //
            if (z < 1e-10) {
                if (z == 0) {
                    result = z;
                } else {
                    final double c = 0.003379167095512573896158903121545171688;
                    result = z * 1.125f + z * c;
                }
            } else {
                // Maximum Deviation Found:                      1.561e-17
                // Expected Error Term:                          1.561e-17
                // Maximum Relative Change in Control Points:    1.155e-04
                // Max Error found at double precision =         2.961182e-17

                final double Y = 1.044948577880859375f;
                final double zz = z * z;
                double P;
                P = -0.000322780120964605683831;
                P =  -0.00772758345802133288487 + P * zz;
                P =   -0.0509990735146777432841 + P * zz;
                P =    -0.338165134459360935041 + P * zz;
                P =    0.0834305892146531832907 + P * zz;
                double Q;
                Q = 0.000370900071787748000569;
                Q =  0.00858571925074406212772 + Q * zz;
                Q =   0.0875222600142252549554 + Q * zz;
                Q =    0.455004033050794024546 + Q * zz;
                Q =                        1.0 + Q * zz;
                result = z * (Y + P / Q);
            }
        // Note: Boost threshold of 5.8f has been raised to approximately 5.93 (6073 / 1024)
        } else if (invert ? (z < 28) : (z < 5.9306640625f)) {
            //
            // We'll be calculating erfc:
            //
            invert = !invert;
            if (z < 1.5f) {
                // Maximum Deviation Found:                     3.702e-17
                // Expected Error Term:                         3.702e-17
                // Maximum Relative Change in Control Points:   2.845e-04
                // Max Error found at double precision =        4.841816e-17
                final double Y = 0.405935764312744140625f;
                final double zm = z - 0.5;
                double P;
                P = 0.00180424538297014223957;
                P =  0.0195049001251218801359 + P * zm;
                P =  0.0888900368967884466578 + P * zm;
                P =   0.191003695796775433986 + P * zm;
                P =   0.178114665841120341155 + P * zm;
                P =  -0.098090592216281240205 + P * zm;
                double Q;
                Q = 0.337511472483094676155e-5;
                Q =   0.0113385233577001411017 + Q * zm;
                Q =     0.12385097467900864233 + Q * zm;
                Q =    0.578052804889902404909 + Q * zm;
                Q =     1.42628004845511324508 + Q * zm;
                Q =     1.84759070983002217845 + Q * zm;
                Q =                        1.0 + Q * zm;
                result = Y + P / Q;
                result *= Math.exp(-z * z) / z;
            } else if (z < 2.5f) {
                // Max Error found at double precision =        6.599585e-18
                // Maximum Deviation Found:                     3.909e-18
                // Expected Error Term:                         3.909e-18
                // Maximum Relative Change in Control Points:   9.886e-05
                final double Y = 0.50672817230224609375f;
                final double zm = z - 1.5;
                double P;
                P = 0.000235839115596880717416;
                P =  0.00323962406290842133584 + P * zm;
                P =   0.0175679436311802092299 + P * zm;
                P =     0.04394818964209516296 + P * zm;
                P =   0.0386540375035707201728 + P * zm;
                P =  -0.0243500476207698441272 + P * zm;
                double Q;
                Q = 0.00410369723978904575884;
                Q =  0.0563921837420478160373 + Q * zm;
                Q =   0.325732924782444448493 + Q * zm;
                Q =   0.982403709157920235114 + Q * zm;
                Q =    1.53991494948552447182 + Q * zm;
                Q =                       1.0 + Q * zm;
                result = Y + P / Q;
                final double sq = z * z;
                final double errSqr = squareLowUnscaled(z, sq);
                result *= Math.exp(-sq) * Math.exp(-errSqr) / z;
            } else if (z < 4.5f) {
                // Maximum Deviation Found:                     1.512e-17
                // Expected Error Term:                         1.512e-17
                // Maximum Relative Change in Control Points:   2.222e-04
                // Max Error found at double precision =        2.062515e-17
                final double Y = 0.5405750274658203125f;
                final double zm = z - 3.5;
                double P;
                P = 0.113212406648847561139e-4;
                P = 0.000250269961544794627958 + P * zm;
                P =  0.00212825620914618649141 + P * zm;
                P =  0.00840807615555585383007 + P * zm;
                P =   0.0137384425896355332126 + P * zm;
                P =  0.00295276716530971662634 + P * zm;
                double Q;
                Q = 0.000479411269521714493907;
                Q =   0.0105982906484876531489 + Q * zm;
                Q =   0.0958492726301061423444 + Q * zm;
                Q =    0.442597659481563127003 + Q * zm;
                Q =     1.04217814166938418171 + Q * zm;
                Q =                        1.0 + Q * zm;
                result = Y + P / Q;
                final double sq = z * z;
                final double errSqr = squareLowUnscaled(z, sq);
                result *= Math.exp(-sq) * Math.exp(-errSqr) / z;
            } else {
                // Max Error found at double precision =        2.997958e-17
                // Maximum Deviation Found:                     2.860e-17
                // Expected Error Term:                         2.859e-17
                // Maximum Relative Change in Control Points:   1.357e-05
                final double Y = 0.5579090118408203125f;
                final double iz = 1 / z;
                double P;
                P =    -2.8175401114513378771;
                P =   -3.22729451764143718517 + P * iz;
                P =    -2.5518551727311523996 + P * iz;
                P =  -0.687717681153649930619 + P * iz;
                P =  -0.212652252872804219852 + P * iz;
                P =  0.0175389834052493308818 + P * iz;
                P = 0.00628057170626964891937 + P * iz;
                double Q;
                Q = 5.48409182238641741584;
                Q = 13.5064170191802889145 + Q * iz;
                Q = 22.9367376522880577224 + Q * iz;
                Q =  15.930646027911794143 + Q * iz;
                Q = 11.0567237927800161565 + Q * iz;
                Q = 2.79257750980575282228 + Q * iz;
                Q =                    1.0 + Q * iz;
                result = Y + P / Q;
                final double sq = z * z;
                final double errSqr = squareLowUnscaled(z, sq);
                result *= Math.exp(-sq) * Math.exp(-errSqr) / z;
            }
        } else {
            //
            // Any value of z larger than 28 will underflow to zero:
            //
            result = 0;
            invert = !invert;
        }

        if (invert) {
            result = 1 - result;
        }

        return result;
    }

    /**
     * Returns the inverse complementary error function.
     *
     * @param z Value (in {@code [0, 2]}).
     * @return t such that {@code z = erfc(t)}
     */
    static double erfcInv(double z) {
        //
        // Begin by testing for domain errors, and other special cases:
        //
        if (z < 0 || z > 2 || Double.isNaN(z)) {
            // Argument outside range [0,2] in inverse erfc function
            return Double.NaN;
        }
        // Domain bounds must be detected as the implementation computes NaN.
        // (log(q=0) creates infinity and the rational number is
        // infinity / infinity)
        if (z == (int) z) {
            // z   return
            // 2   -inf
            // 1   0
            // 0   inf
            return z == 1 ? 0 : (1 - z) * Double.POSITIVE_INFINITY;
        }

        //
        // Normalise the input, so it's in the range [0,1], we will
        // negate the result if z is outside that range. This is a simple
        // application of the erfc reflection formula: erfc(-z) = 2 - erfc(z)
        //
        double p;
        double q;
        double s;
        if (z > 1) {
            q = 2 - z;
            p = 1 - q;
            s = -1;
        } else {
            p = 1 - z;
            q = z;
            s = 1;
        }

        //
        // And get the result, negating where required:
        //
        return s * erfInvImp(p, q);
    }

    /**
     * Returns the inverse error function.
     *
     * @param z Value (in {@code [-1, 1]}).
     * @return t such that {@code z = erf(t)}
     */
    static double erfInv(double z) {
        //
        // Begin by testing for domain errors, and other special cases:
        //
        if (z < -1 || z > 1 || Double.isNaN(z)) {
            // Argument outside range [-1, 1] in inverse erf function
            return Double.NaN;
        }
        // Domain bounds must be detected as the implementation computes NaN.
        // (log(q=0) creates infinity and the rational number is
        // infinity / infinity)
        if (z == (int) z) {
            // z   return
            // -1  -inf
            // -0  -0
            // 0   0
            // 1   inf
            return z == 0 ? z : z * Double.POSITIVE_INFINITY;
        }

        //
        // Normalise the input, so it's in the range [0,1], we will
        // negate the result if z is outside that range. This is a simple
        // application of the erf reflection formula: erf(-z) = -erf(z)
        //
        double p;
        double q;
        double s;
        if (z < 0) {
            p = -z;
            q = 1 - p;
            s = -1;
        } else {
            p = z;
            q = 1 - z;
            s = 1;
        }
        //
        // And get the result, negating where required:
        //
        return s * erfInvImp(p, q);
    }

    /**
     * Common implementation for inverse erf and erfc functions.
     *
     * @param p P-value
     * @param q Q-value (1-p)
     * @return the inverse
     */
    private static double erfInvImp(double p, double q) {
        double result = 0;

        if (p <= 0.5) {
            //
            // Evaluate inverse erf using the rational approximation:
            //
            // x = p(p+10)(Y+R(p))
            //
            // Where Y is a constant, and R(p) is optimised for a low
            // absolute error compared to |Y|.
            //
            // double: Max error found: 2.001849e-18
            // long double: Max error found: 1.017064e-20
            // Maximum Deviation Found (actual error term at infinite precision) 8.030e-21
            //
            final float Y = 0.0891314744949340820313f;
            double P;
            P =  -0.00538772965071242932965;
            P =   0.00822687874676915743155 + P * p;
            P =    0.0219878681111168899165 + P * p;
            P =   -0.0365637971411762664006 + P * p;
            P =   -0.0126926147662974029034 + P * p;
            P =    0.0334806625409744615033 + P * p;
            P =  -0.00836874819741736770379 + P * p;
            P = -0.000508781949658280665617 + P * p;
            double Q;
            Q = 0.000886216390456424707504;
            Q = -0.00233393759374190016776 + Q * p;
            Q =   0.0795283687341571680018 + Q * p;
            Q =  -0.0527396382340099713954 + Q * p;
            Q =    -0.71228902341542847553 + Q * p;
            Q =    0.662328840472002992063 + Q * p;
            Q =     1.56221558398423026363 + Q * p;
            Q =    -1.56574558234175846809 + Q * p;
            Q =   -0.970005043303290640362 + Q * p;
            Q =                        1.0 + Q * p;
            final double g = p * (p + 10);
            final double r = P / Q;
            result = g * Y + g * r;
        } else if (q >= 0.25) {
            //
            // Rational approximation for 0.5 > q >= 0.25
            //
            // x = sqrt(-2*log(q)) / (Y + R(q))
            //
            // Where Y is a constant, and R(q) is optimised for a low
            // absolute error compared to Y.
            //
            // double : Max error found: 7.403372e-17
            // long double : Max error found: 6.084616e-20
            // Maximum Deviation Found (error term) 4.811e-20
            //
            final float Y = 2.249481201171875f;
            final double xs = q - 0.25f;
            double P;
            P =  -3.67192254707729348546;
            P =   21.1294655448340526258 + P * xs;
            P =    17.445385985570866523 + P * xs;
            P =  -44.6382324441786960818 + P * xs;
            P =  -18.8510648058714251895 + P * xs;
            P =   17.6447298408374015486 + P * xs;
            P =   8.37050328343119927838 + P * xs;
            P =  0.105264680699391713268 + P * xs;
            P = -0.202433508355938759655 + P * xs;
            double Q;
            Q =  1.72114765761200282724;
            Q = -22.6436933413139721736 + Q * xs;
            Q =  10.8268667355460159008 + Q * xs;
            Q =  48.5609213108739935468 + Q * xs;
            Q = -20.1432634680485188801 + Q * xs;
            Q = -28.6608180499800029974 + Q * xs;
            Q =   3.9713437953343869095 + Q * xs;
            Q =  6.24264124854247537712 + Q * xs;
            Q =                     1.0 + Q * xs;
            final double g = Math.sqrt(-2 * Math.log(q));
            final double r = P / Q;
            result = g / (Y + r);
        } else {
            //
            // For q < 0.25 we have a series of rational approximations all
            // of the general form:
            //
            // let: x = sqrt(-log(q))
            //
            // Then the result is given by:
            //
            // x(Y+R(x-B))
            //
            // where Y is a constant, B is the lowest value of x for which
            // the approximation is valid, and R(x-B) is optimised for a low
            // absolute error compared to Y.
            //
            // Note that almost all code will really go through the first
            // or maybe second approximation. After than we're dealing with very
            // small input values indeed.
            //
            // Limit for a double: Math.sqrt(-Math.log(Double.MIN_VALUE)) = 27.28...
            // Branches for x >= 44 (supporting 80 and 128 bit long double) have been removed.
            final double x = Math.sqrt(-Math.log(q));
            if (x < 3) {
                // Max error found: 1.089051e-20
                final float Y = 0.807220458984375f;
                final double xs = x - 1.125f;
                double P;
                P = -0.681149956853776992068e-9;
                P =  0.285225331782217055858e-7 + P * xs;
                P = -0.679465575181126350155e-6 + P * xs;
                P =   0.00214558995388805277169 + P * xs;
                P =    0.0290157910005329060432 + P * xs;
                P =     0.142869534408157156766 + P * xs;
                P =     0.337785538912035898924 + P * xs;
                P =     0.387079738972604337464 + P * xs;
                P =     0.117030156341995252019 + P * xs;
                P =    -0.163794047193317060787 + P * xs;
                P =    -0.131102781679951906451 + P * xs;
                double Q;
                Q =  0.01105924229346489121;
                Q = 0.152264338295331783612 + Q * xs;
                Q = 0.848854343457902036425 + Q * xs;
                Q =  2.59301921623620271374 + Q * xs;
                Q =  4.77846592945843778382 + Q * xs;
                Q =  5.38168345707006855425 + Q * xs;
                Q =  3.46625407242567245975 + Q * xs;
                Q =                     1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            } else if (x < 6) {
                // Max error found: 8.389174e-21
                final float Y = 0.93995571136474609375f;
                final double xs = x - 3;
                double P;
                P = 0.266339227425782031962e-11;
                P = -0.230404776911882601748e-9 + P * xs;
                P =  0.460469890584317994083e-5 + P * xs;
                P =  0.000157544617424960554631 + P * xs;
                P =   0.00187123492819559223345 + P * xs;
                P =   0.00950804701325919603619 + P * xs;
                P =    0.0185573306514231072324 + P * xs;
                P =  -0.00222426529213447927281 + P * xs;
                P =   -0.0350353787183177984712 + P * xs;
                double Q;
                Q = 0.764675292302794483503e-4;
                Q =  0.00263861676657015992959 + Q * xs;
                Q =   0.0341589143670947727934 + Q * xs;
                Q =    0.220091105764131249824 + Q * xs;
                Q =    0.762059164553623404043 + Q * xs;
                Q =      1.3653349817554063097 + Q * xs;
                Q =                        1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            } else if (x < 18) {
                // Max error found: 1.481312e-19
                final float Y = 0.98362827301025390625f;
                final double xs = x - 6;
                double P;
                P =   0.99055709973310326855e-16;
                P = -0.281128735628831791805e-13 + P * xs;
                P =   0.462596163522878599135e-8 + P * xs;
                P =   0.449696789927706453732e-6 + P * xs;
                P =   0.149624783758342370182e-4 + P * xs;
                P =   0.000209386317487588078668 + P * xs;
                P =    0.00105628862152492910091 + P * xs;
                P =   -0.00112951438745580278863 + P * xs;
                P =    -0.0167431005076633737133 + P * xs;
                double Q;
                Q = 0.282243172016108031869e-6;
                Q = 0.275335474764726041141e-4 + Q * xs;
                Q = 0.000964011807005165528527 + Q * xs;
                Q =   0.0160746087093676504695 + Q * xs;
                Q =    0.138151865749083321638 + Q * xs;
                Q =    0.591429344886417493481 + Q * xs;
                Q =                        1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            } else {
                // x < 44
                // Max error found: 5.697761e-20
                final float Y = 0.99714565277099609375f;
                final double xs = x - 18;
                double P;
                P = -0.116765012397184275695e-17;
                P =  0.145596286718675035587e-11 + P * xs;
                P =   0.411632831190944208473e-9 + P * xs;
                P =   0.396341011304801168516e-7 + P * xs;
                P =   0.162397777342510920873e-5 + P * xs;
                P =   0.254723037413027451751e-4 + P * xs;
                P =  -0.779190719229053954292e-5 + P * xs;
                P =    -0.0024978212791898131227 + P * xs;
                double Q;
                Q = 0.509761276599778486139e-9;
                Q = 0.144437756628144157666e-6 + Q * xs;
                Q = 0.145007359818232637924e-4 + Q * xs;
                Q = 0.000690538265622684595676 + Q * xs;
                Q =   0.0169410838120975906478 + Q * xs;
                Q =    0.207123112214422517181 + Q * xs;
                Q =                        1.0 + Q * xs;
                final double R = P / Q;
                result = Y * x + R * x;
            }
        }
        return result;
    }

    // Extended precision multiplication specialised for the square adapted from:
    // org.apache.commons.numbers.core.ExtendedPrecision

    /**
     * Compute the low part of the double length number {@code (z,zz)} for the exact
     * square of {@code x} using Dekker's mult12 algorithm. The standard precision product
     * {@code x*x} must be provided. The number {@code x} is split into high and low parts
     * using Dekker's algorithm.
     *
     * <p>Warning: This method does not perform scaling in Dekker's split and large
     * finite numbers can create NaN results.
     *
     * @param x Number to square
     * @param xx Standard precision product {@code x*x}
     * @return the low part of the square double length number
     */
    private static double squareLowUnscaled(double x, double xx) {
        // Split the numbers using Dekker's algorithm without scaling
        final double hx = highPartUnscaled(x);
        final double lx = x - hx;

        return squareLow(hx, lx, xx);
    }

    /**
     * Implement Dekker's method to split a value into two parts. Multiplying by (2^s + 1) creates
     * a big value from which to derive the two split parts.
     * <pre>
     * c = (2^s + 1) * a
     * a_big = c - a
     * a_hi = c - a_big
     * a_lo = a - a_hi
     * a = a_hi + a_lo
     * </pre>
     *
     * <p>The multiplicand allows a p-bit value to be split into
     * (p-s)-bit value {@code a_hi} and a non-overlapping (s-1)-bit value {@code a_lo}.
     * Combined they have (p-1) bits of significand but the sign bit of {@code a_lo}
     * contains a bit of information. The constant is chosen so that s is ceil(p/2) where
     * the precision p for a double is 53-bits (1-bit of the mantissa is assumed to be
     * 1 for a non sub-normal number) and s is 27.
     *
     * <p>This conversion does not use scaling and the result of overflow is NaN. Overflow
     * may occur when the exponent of the input value is above 996.
     *
     * <p>Splitting a NaN or infinite value will return NaN.
     *
     * @param value Value.
     * @return the high part of the value.
     * @see Math#getExponent(double)
     */
    private static double highPartUnscaled(double value) {
        final double c = MULTIPLIER * value;
        return c - (c - value);
    }

    /**
     * Compute the low part of the double length number {@code (z,zz)} for the exact
     * square of {@code x} using Dekker's mult12 algorithm. The standard
     * precision product {@code x*x} must be provided. The number {@code x}
     * should already be split into low and high parts.
     *
     * <p>Note: This uses the high part of the result {@code (z,zz)} as {@code x * x} and not
     * {@code hx * hx + hx * lx + lx * hx} as specified in Dekker's original paper.
     * See Shewchuk (1997) for working examples.
     *
     * @param hx High part of factor.
     * @param lx Low part of factor.
     * @param xx Square of the factor.
     * @return <code>lx * ly - (((xy - hx * hy) - lx * hy) - hx * ly)</code>
     * @see <a href="http://www-2.cs.cmu.edu/afs/cs/project/quake/public/papers/robust-arithmetic.ps">
     * Shewchuk (1997) Theorum 18</a>
     */
    private static double squareLow(double hx, double lx, double xx) {
        // Compute the multiply low part:
        // err1 = xy - hx * hy
        // err2 = err1 - lx * hy
        // err3 = err2 - hx * ly
        // low = lx * ly - err3
        return lx * lx - ((xx - hx * hx) - 2 * lx * hx);
    }
}

