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

package org.apache.commons.numbers.complex;

import org.apache.commons.numbers.core.Precision;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Tests the standards defined by the C.99 standard for complex numbers
 * defined in ISO/IEC 9899, Annex G.
 *
 * @see <a href="http://www.open-std.org/JTC1/SC22/WG14/www/standards">
 *    ISO/IEC 9899 - Programming languages - C</a>
 */
public class CStandardTest {

    // CHECKSTYLE: stop ConstantName
    private static final double inf = Double.POSITIVE_INFINITY;
    private static final double negInf = Double.NEGATIVE_INFINITY;
    private static final double nan = Double.NaN;
    private static final double max = Double.MAX_VALUE;
    private static final double piOverFour = Math.PI / 4.0;
    private static final double piOverTwo = Math.PI / 2.0;
    private static final double threePiOverFour = 3.0 * Math.PI / 4.0;
    private static final Complex oneOne = complex(1, 1);
    private static final Complex oneZero = complex(1, 0);
    private static final Complex oneInf = complex(1, inf);
    private static final Complex oneNaN = complex(1, nan);
    private static final Complex zeroInf = complex(0, inf);
    private static final Complex zeroNegInf = complex(0, negInf);
    private static final Complex zeroNaN = complex(0, nan);
    private static final Complex zeroPiTwo = complex(0.0, piOverTwo);
    private static final Complex negZeroZero = complex(-0.0, 0);
    private static final Complex negI = complex(0.0, -1.0);
    private static final Complex infOne = complex(inf, 1);
    private static final Complex infZero = complex(inf, 0);
    private static final Complex infNaN = complex(inf, nan);
    private static final Complex infInf = complex(inf, inf);
    private static final Complex infPiTwo = complex(inf, piOverTwo);
    private static final Complex infThreePiFour = complex(inf, threePiOverFour);
    private static final Complex infPiFour = complex(inf, piOverFour);
    private static final Complex infPi = complex(inf, Math.PI);
    private static final Complex negInfInf = complex(negInf, inf);
    private static final Complex negInfZero = complex(negInf, 0);
    private static final Complex negInfOne = complex(negInf, 1);
    private static final Complex negInfNaN = complex(negInf, nan);
    private static final Complex negInfPosInf = complex(negInf, inf);
    private static final Complex negInfPi = complex(negInf, Math.PI);
    private static final Complex nanInf = complex(nan, inf);
    private static final Complex nanNegInf = complex(nan, negInf);
    private static final Complex nanZero = complex(nan, 0);
    private static final Complex nanOne = complex(nan, 1);
    private static final Complex piTwoNaN = complex(piOverTwo, nan);
    private static final Complex piNegInf = complex(Math.PI, negInf);
    private static final Complex piTwoNegInf = complex(piOverTwo, negInf);
    private static final Complex piTwoNegZero = complex(piOverTwo, -0.0);
    private static final Complex threePiFourNegInf = complex(threePiOverFour, negInf);
    private static final Complex piFourNegInf = complex(piOverFour, negInf);
    private static final Complex NAN = complex(nan, nan);
    private static final Complex maxMax = complex(max, max);
    private static final Complex maxNan = complex(max, nan);
    private static final Complex nanMax = complex(nan, max);
    // CHECKSTYLE: resume ConstantName

    /**
     * Assert the two complex numbers have their real and imaginary components within
     * the given tolerance.
     *
     * @param c1 the first complex
     * @param c2 the second complex
     * @param maxUlps {@code (maxUlps - 1)} is the number of floating point
     * values between the real (resp. imaginary) parts of {@code x} and
     * {@code y}.
     */
    public void assertComplex(Complex c1, Complex c2, int maxUlps) {
        if (!Precision.equals(c1.getReal(), c2.getReal(), maxUlps) ||
            !Precision.equals(c1.getImaginary(), c2.getImaginary(), maxUlps)) {
            Assertions.fail(c1 + " != " + c2);
        }
    }

    /**
     * Assert the two complex numbers have equivalent real and imaginary components as
     * defined by the {@code ==} operator.
     *
     * @param c1 the first complex
     * @param c2 the second complex
     */
    public void assertComplex(Complex c1, Complex c2) {
        // Use a delta of zero to allow comparison of -0.0 to 0.0
        Assertions.assertEquals(c1.getReal(), c2.getReal(), 0.0, "real");
        Assertions.assertEquals(c1.getImaginary(), c2.getImaginary(), 0.0, "imaginary");
    }

    /**
     * Assert the operation on the two complex numbers.
     *
     * @param c1 the first complex
     * @param c2 the second complex
     * @param operation the operation
     * @param operationName the operation name
     * @param expected the expected
     * @param expectedName the expected name
     */
    private static void assertOperation(Complex c1, Complex c2,
            BiFunction<Complex, Complex, Complex> operation, String operationName,
            Predicate<Complex> expected, String expectedName) {
        final Complex z = operation.apply(c1, c2);
        Assertions.assertTrue(expected.test(z),
            () -> String.format("%s expected: %s %s %s = %s", expectedName, c1, operationName, c2, z));
    }

    /**
     * Utility to create a Complex.
     *
     * @param real the real
     * @param imaginary the imaginary
     * @return the complex
     */
    private static Complex complex(double real, double imaginary) {
        return Complex.ofCartesian(real, imaginary);
    }

    /**
     * Creates a list of Complex infinites.
     *
     * @return the list
     */
    private static ArrayList<Complex> createInfinites() {
        final double[] values = {0, 1, inf, negInf, nan};
        return createCombinations(values, Complex::isInfinite);
    }

    /**
     * Creates a list of Complex finites that are not zero.
     *
     * @return the list
     */
    private static ArrayList<Complex> createNonZeroFinites() {
        final double[] values = {-1, -0, 0, 1, Double.MAX_VALUE};
        return createCombinations(values, c -> !CStandardTest.isZero(c));
    }

    /**
     * Creates a list of Complex finites that are zero: [0,0], [-0,0], [0,-0], [-0,-0].
     *
     * @return the list
     */
    private static ArrayList<Complex> createZeroFinites() {
        final double[] values = {-0, 0};
        return createCombinations(values, c -> true);
    }

    /**
     * Creates a list of Complex NaNs.
     *
     * @return the list
     */
    private static ArrayList<Complex> createNaNs() {
        final double[] values = {0, 1, nan};
        return createCombinations(values, Complex::isNaN);
    }

    /**
     * Creates a list of Complex numbers as an all-vs-all combinations that pass the
     * condition.
     *
     * @param values the values
     * @param condition the condition
     * @return the list
     */
    private static ArrayList<Complex> createCombinations(final double[] values, Predicate<Complex> condition) {
        final ArrayList<Complex> list = new ArrayList<>();
        for (double re : values) {
            for (double im : values) {
                final Complex z = complex(re, im);
                if (condition.test(z)) {
                    list.add(z);
                }
            }
        }
        return list;
    }

    /**
     * Checks if the complex is zero.
     *
     * @param c the complex
     * @return true if zero
     */
    private static boolean isZero(Complex c) {
        return Complex.equals(c, Complex.ZERO, 0);
    }

    /**
     * ISO C Standard G.5 (4).
     */
    @Test
    public void testMultiply() {
        final ArrayList<Complex> infinites = createInfinites();
        final ArrayList<Complex> nonZeroFinites = createNonZeroFinites();
        final ArrayList<Complex> zeroFinites = createZeroFinites();

        // C.99 refers to non-zero finites.
        // Standard multiplication of zero with infinites is not defined.
        Assertions.assertEquals(nan, 0.0 * inf, "0 * inf");
        Assertions.assertEquals(nan, 0.0 * negInf, "0 * -inf");
        Assertions.assertEquals(nan, -0.0 * inf, "-0 * inf");
        Assertions.assertEquals(nan, -0.0 * negInf, "-0 * -inf");

        // "if one operand is an infinity and the other operand is a nonzero finite number or an
        // infinity, then the result of the * operator is an infinity;"
        for (Complex z : infinites) {
            for (Complex w : infinites) {
                assertOperation(z, w, Complex::multiply, "*", Complex::isInfinite, "Inf");
                assertOperation(w, z, Complex::multiply, "*", Complex::isInfinite, "Inf");
            }
            for (Complex w : nonZeroFinites) {
                assertOperation(z, w, Complex::multiply, "*", Complex::isInfinite, "Inf");
                assertOperation(w, z, Complex::multiply, "*", Complex::isInfinite, "Inf");
            }
            // C.99 refers to non-zero finites.
            // Infer that Complex multiplication of zero with infinites is not defined.
            for (Complex w : zeroFinites) {
                assertOperation(z, w, Complex::multiply, "*", Complex::isNaN, "NaN");
                assertOperation(w, z, Complex::multiply, "*", Complex::isNaN, "NaN");
            }
        }

        // ISO C Standard in Annex G is missing an explicit definition of how to handle NaNs.
        // We will assume multiplication by (nan,nan) is not allowed.
        // It is undefined how to multiply when a complex has only one NaN component.
        // The reference implementation allows it.

        // The GNU g++ compiler computes:
        // (1e300 + i 1e300) * (1e30 + i NAN) = inf + i inf
        // Thus this is allowing some computations with NaN.

        // Check multiply with (NaN,NaN) is not corrected
        final double[] values = {0, 1, inf, negInf, nan};
        for (Complex z : createCombinations(values, c -> true)) {
            assertOperation(z, NAN, Complex::multiply, "*", Complex::isNaN, "NaN");
            assertOperation(NAN, z, Complex::multiply, "*", Complex::isNaN, "NaN");
        }

        // Test multiply cases which result in overflow are corrected to infinity
        assertOperation(maxMax, maxMax, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(maxNan, maxNan, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(nanMax, maxNan, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(maxNan, nanMax, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(nanMax, nanMax, Complex::multiply, "*", Complex::isInfinite, "Inf");
    }

    /**
     * ISO C Standard G.5 (4).
     */
    @Test
    public void testDivide() {
        final ArrayList<Complex> infinites = createInfinites();
        final ArrayList<Complex> nonZeroFinites = createNonZeroFinites();
        final ArrayList<Complex> zeroFinites = createZeroFinites();
        final ArrayList<Complex> nans = createNaNs();
        final ArrayList<Complex> finites = new ArrayList<>(nonZeroFinites);
        finites.addAll(zeroFinites);

        // "if the first operand is an infinity and the second operand is a finite number, then the
        // result of the / operator is an infinity;"
        for (Complex z : infinites) {
            for (Complex w : nonZeroFinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }
            for (Complex w : zeroFinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }
            // Check inf/inf cannot be done
            for (Complex w : infinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isNaN, "NaN");
            }
        }

        // "if the first operand is a finite number and the second operand is an infinity, then the
        // result of the / operator is a zero;"
        for (Complex z : finites) {
            for (Complex w : infinites) {
                assertOperation(z, w, Complex::divide, "/", CStandardTest::isZero, "Zero");
            }
        }

        // "if the first operand is a nonzero finite number or an infinity and the second operand is
        // a zero, then the result of the / operator is an infinity."
        for (Complex w : zeroFinites) {
            for (Complex z : nonZeroFinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }
            for (Complex z : infinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }
        }

        // ISO C Standard in Annex G is missing an explicit definition of how to handle NaNs.
        // The reference implementation does not correct for divide by NaN components unless
        // infinite.
        for (Complex w : nans) {
            for (Complex z : finites) {
                assertOperation(z, w, Complex::divide, "/", c -> NAN.equals(c), "(NaN,NaN)");
            }
            for (Complex z : infinites) {
                assertOperation(z, w, Complex::divide, "/", c -> NAN.equals(c), "(NaN,NaN)");
            }
        }

        // Check (NaN,NaN) divide is not corrected for the edge case of divide by zero or infinite
        for (Complex w : zeroFinites) {
            assertOperation(NAN, w, Complex::divide, "/", Complex::isNaN, "NaN");
        }
        for (Complex w : infinites) {
            assertOperation(NAN, w, Complex::divide, "/", Complex::isNaN, "NaN");
        }
    }

    /**
     * ISO C Standard G.6 (3).
     */
    @Test
    public void testSqrt1() {
        assertComplex(complex(-2.0, 0.0).sqrt(), complex(0.0, Math.sqrt(2)));
        assertComplex(complex(-2.0, -0.0).sqrt(), complex(0.0, -Math.sqrt(2)));
    }

    /**
     * ISO C Standard G.6 (7).
     */
    @Test
    public void testImplicitTrig() {
        final UniformRandomProvider rng = RandomSource.create(RandomSource.SPLIT_MIX_64);
        for (int i = 0; i < 100; i++) {
            final double re = next(rng);
            final double im = next(rng);
            final Complex z = complex(re, im);
            final Complex iz = Complex.I.multiply(z);
            assertComplex(z.asin(), negI.multiply(iz.asinh()));
            assertComplex(z.atan(), negI.multiply(iz.atanh()));
            assertComplex(z.cos(), iz.cosh());
            assertComplex(z.sin(), negI.multiply(iz.sinh()));
            assertComplex(z.tan(), negI.multiply(iz.tanh()));
        }
    }

    /**
     * Create a number in the range {@code (-1,1)}.
     *
     * @param rng the random generator
     * @return the number
     */
    private static double next(UniformRandomProvider rng) {
        return rng.nextDouble() * (rng.nextBoolean() ? -1 : 1);
    }

    /**
     * ISO C Standard G.6 (6) for abs().
     * Defined by ISO C Standard F.9.4.3 hypot function.
     */
    @Test
    public void testAbs() {
        Assertions.assertEquals(inf, complex(inf, nan).abs());
        Assertions.assertEquals(inf, complex(negInf, nan).abs());
        final UniformRandomProvider rng = RandomSource.create(RandomSource.SPLIT_MIX_64);
        for (int i = 0; i < 100; i++) {
            final double x = next(rng);
            final double y = next(rng);
            Assertions.assertEquals(complex(x, y).abs(), complex(y, x).abs());
            Assertions.assertEquals(complex(x, y).abs(), complex(x, -y).abs());
            Assertions.assertEquals(Math.abs(x), complex(x, 0.0).abs());
            Assertions.assertEquals(Math.abs(x), complex(x, -0.0).abs());
            Assertions.assertEquals(inf, complex(inf, y).abs());
            Assertions.assertEquals(inf, complex(negInf, y).abs());
        }
    }

    /**
     * ISO C Standard G.6.1.1.
     */
    @Test
    public void testAcos() {
        assertComplex(oneOne.acos().conj(), oneOne.conj().acos(), 1);
        assertComplex(Complex.ZERO.acos(), piTwoNegZero);
        assertComplex(negZeroZero.acos(), piTwoNegZero);
        assertComplex(zeroNaN.acos(), piTwoNaN);
        assertComplex(oneInf.acos(), piTwoNegInf);
        assertComplex(oneNaN.acos(), NAN);
        assertComplex(negInfOne.acos(), piNegInf);
        assertComplex(infOne.acos(), zeroNegInf);
        assertComplex(negInfPosInf.acos(), threePiFourNegInf);
        assertComplex(infInf.acos(), piFourNegInf);
        assertComplex(infNaN.acos(), nanInf);
        assertComplex(negInfNaN.acos(), nanNegInf);
        assertComplex(nanOne.acos(), NAN);
        assertComplex(nanInf.acos(), nanNegInf);
        assertComplex(NAN.acos(), NAN);
    }

    /**
     * ISO C Standard G.6.2.1.
     */
    @Test
    public void testAcosh() {
        assertComplex(oneOne.acosh().conj(), oneOne.conj().acosh(), 1);
        assertComplex(Complex.ZERO.acosh(), zeroPiTwo);
        assertComplex(negZeroZero.acosh(), zeroPiTwo);
        assertComplex(oneInf.acosh(), infPiTwo);
        assertComplex(zeroNaN.acosh(), NAN);
        assertComplex(oneNaN.acosh(), NAN);
        assertComplex(negInfOne.acosh(), infPi);
        assertComplex(negInfZero.acosh(), infPi);
        assertComplex(infOne.acosh(), infZero);
        assertComplex(infZero.acosh(), infZero);
        assertComplex(negInfPosInf.acosh(), infThreePiFour);
        assertComplex(infInf.acosh(), infPiFour);
        assertComplex(infNaN.acosh(), infNaN);
        assertComplex(negInfNaN.acosh(), infNaN);
        assertComplex(nanOne.acosh(), NAN);
        assertComplex(nanInf.acosh(), infNaN);
        assertComplex(NAN.acosh(), NAN);
        // The standard mentions positive-signed y
        Assertions.assertThrows(AssertionError.class, () -> {
            // Not −∞ + iy, positive signed y
            assertComplex(complex(negInf, -0.0).acosh(), infPi);
        });
        Assertions.assertThrows(AssertionError.class, () -> {
            // Not ∞ + iy, positive signed y
            assertComplex(complex(inf, -0.0).acosh(), infPi);
        });
    }

    /**
     * ISO C Standard G.6.2.2.
     */
    @Test
    public void testAsinh() {
        // TODO: test for which Asinh is odd
        assertComplex(oneOne.conj().asinh(), oneOne.asinh().conj());
        assertComplex(Complex.ZERO.asinh(), Complex.ZERO);
        assertComplex(oneInf.asinh(), infPiTwo);
        assertComplex(oneNaN.asinh(), NAN);
        assertComplex(infOne.asinh(), infZero);
        assertComplex(infInf.asinh(), infPiFour);
        assertComplex(infNaN.asinh(), infNaN);
        assertComplex(nanZero.asinh(), nanZero);
        assertComplex(nanOne.asinh(), NAN);
        assertComplex(nanInf.asinh(), infNaN);
        assertComplex(NAN, NAN);
    }

    /**
     * ISO C Standard G.6.2.3.
     */
    @Test
    public void testAtanh() {
        assertComplex(oneOne.conj().atanh(), oneOne.atanh().conj());
        assertComplex(Complex.ZERO.atanh(), Complex.ZERO);
        assertComplex(zeroNaN.atanh(), zeroNaN);
        assertComplex(oneZero.atanh(), infZero);
        assertComplex(oneInf.atanh(), zeroPiTwo);
        assertComplex(oneNaN.atanh(), NAN);
        assertComplex(infOne.atanh(), zeroPiTwo);
        assertComplex(infInf.atanh(), zeroPiTwo);
        assertComplex(infNaN.atanh(), zeroNaN);
        assertComplex(nanOne.atanh(), NAN);
        assertComplex(nanInf.atanh(), zeroPiTwo);
        assertComplex(NAN.atanh(), NAN);
    }

    /**
     * ISO C Standard G.6.2.4.
     */
    @Test
    public void testCosh() {
        assertComplex(oneOne.cosh().conj(), oneOne.conj().cosh());
        assertComplex(Complex.ZERO.cosh(), Complex.ONE);
        assertComplex(zeroInf.cosh(), nanZero);
        assertComplex(zeroNaN.cosh(), nanZero);
        assertComplex(oneInf.cosh(), NAN);
        assertComplex(oneNaN.cosh(), NAN);
        assertComplex(infZero.cosh(), infZero);
        // the next test does not appear to make sense:
        // (inf + iy) = inf + cis(y)
        // skipped
        assertComplex(infInf.cosh(), infNaN);
        assertComplex(infNaN.cosh(), infNaN);
        assertComplex(nanZero.cosh(), nanZero);
        assertComplex(nanOne.cosh(), NAN);
        assertComplex(NAN.cosh(), NAN);
    }

    /**
     * ISO C Standard G.6.2.5.
     */
    @Test
    public void testSinh() {
        assertComplex(oneOne.sinh().conj(), oneOne.conj().sinh()); // AND CSINH IS ODD
        assertComplex(Complex.ZERO.sinh(), Complex.ZERO);
        assertComplex(zeroInf.sinh(), zeroNaN);
        assertComplex(zeroNaN.sinh(), zeroNaN);
        assertComplex(oneInf.sinh(), NAN);
        assertComplex(oneNaN.sinh(), NAN);
        assertComplex(infZero.sinh(), infZero);
        // skipped test similar to previous section
        assertComplex(infInf.sinh(), infNaN);
        assertComplex(infNaN.sinh(), infNaN);
        assertComplex(nanZero.sinh(), nanZero);
        assertComplex(nanOne.sinh(), NAN);
        assertComplex(NAN.sinh(), NAN);
    }

    /**
     * ISO C Standard G.6.2.6.
     */
    @Test
    public void testTanh() {
        assertComplex(oneOne.tanh().conj(), oneOne.conj().tanh()); // AND CSINH IS ODD
        assertComplex(Complex.ZERO.tanh(), Complex.ZERO);
        assertComplex(oneInf.tanh(), NAN);
        assertComplex(oneNaN.tanh(), NAN);
        //Do Not Understand the Next Test
        assertComplex(infInf.tanh(), oneZero);
        assertComplex(infNaN.tanh(), oneZero);
        assertComplex(nanZero.tanh(), nanZero);
        assertComplex(nanOne.tanh(), NAN);
        assertComplex(NAN.tanh(), NAN);
    }

    /**
     * ISO C Standard G.6.3.1.
     */
    @Test
    public void testExp() {
        assertComplex(oneOne.conj().exp(), oneOne.exp().conj());
        assertComplex(Complex.ZERO.exp(), oneZero);
        assertComplex(negZeroZero.exp(), oneZero);
        assertComplex(oneInf.exp(), NAN);
        assertComplex(oneNaN.exp(), NAN);
        assertComplex(infZero.exp(), infZero);
        // Do not understand next test
        assertComplex(negInfInf.exp(), Complex.ZERO);
        assertComplex(infInf.exp(), infNaN);
        assertComplex(negInfNaN.exp(), Complex.ZERO);
        assertComplex(infNaN.exp(), infNaN);
        assertComplex(nanZero.exp(), nanZero);
        assertComplex(nanOne.exp(), NAN);
        assertComplex(NAN.exp(), NAN);
    }

    /**
     * ISO C Standard G.6.3.2.
     */
    @Test
    public void testLog() {
        assertComplex(oneOne.log().conj(), oneOne.conj().log());
        assertComplex(negZeroZero.log(), negInfPi);
        assertComplex(Complex.ZERO.log(), negInfZero);
        assertComplex(oneInf.log(), infPiTwo);
        assertComplex(oneNaN.log(), NAN);
        assertComplex(negInfOne.log(), infPi);
        assertComplex(infOne.log(), infZero);
        assertComplex(infInf.log(), infPiFour);
        assertComplex(infNaN.log(), infNaN);
        assertComplex(nanOne.log(), NAN);
        assertComplex(nanInf.log(), infNaN);
        assertComplex(NAN.log(), NAN);
    }

    /**
     * Same edge cases as log() since the real component is divided by Math.log(10) whic
     * has no effect on infinite or nan.
     */
    @Test
    public void testLog10() {
        assertComplex(oneOne.log10().conj(), oneOne.conj().log10());
        assertComplex(negZeroZero.log10(), negInfPi);
        assertComplex(Complex.ZERO.log10(), negInfZero);
        assertComplex(oneInf.log10(), infPiTwo);
        assertComplex(oneNaN.log10(), NAN);
        assertComplex(negInfOne.log10(), infPi);
        assertComplex(infOne.log10(), infZero);
        assertComplex(infInf.log10(), infPiFour);
        assertComplex(infNaN.log10(), infNaN);
        assertComplex(nanOne.log10(), NAN);
        assertComplex(nanInf.log10(), infNaN);
        assertComplex(NAN.log10(), NAN);
    }

    /**
     * ISO C Standard G.6.4.2.
     */
    @Test
    public void testSqrt2() {
        assertComplex(oneOne.sqrt().conj(), oneOne.conj().sqrt());
        assertComplex(Complex.ZERO.sqrt(), Complex.ZERO);
        assertComplex(oneInf.sqrt(), infInf);
        assertComplex(negInfOne.sqrt(), zeroNaN);
        assertComplex(infOne.sqrt(), infZero);
        assertComplex(negInfNaN.sqrt(), nanInf);
        assertComplex(infNaN.sqrt(), infNaN);
        assertComplex(nanOne.sqrt(), NAN);
        assertComplex(NAN.sqrt(), NAN);
    }
}
