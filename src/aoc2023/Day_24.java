package aoc2023;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import util.Helpers;
import util.Resources;
import util.TaskData;
import util.TaskSolution;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class Day_24 {

    private static final TaskData TEST = new TaskData(Resources.getResourceAsString("aoc2023/24-test.txt"), 2, 47);
    private static final TaskData MAIN = new TaskData(Resources.getResourceAsString("aoc2023/24-main.txt"), 15593, 757031940316991L);

    private static final Pair<Long, Long> RANGE_MAIN = Pair.of(200000000000000L, 400000000000000L);

    private static final Pair<Long, Long> RANGE_TEST = Pair.of(7L, 27L);

    private record IntExpr3D(IntExpr x, IntExpr y, IntExpr z) {
    }

    private record Point3D(BigInteger x, BigInteger y, BigInteger z) {
    }

    private record Vec3D(BigInteger dX, BigInteger dY, BigInteger dZ) {
    }

    private record Point2D(BigInteger x, BigInteger y) {
        public Point2D(Point3D point3D) {
            this(point3D.x(), point3D.y());
        }
    }

    private record Vec2D(BigInteger dX, BigInteger dY) {
        public Vec2D(Vec3D vec3D) {
            this(vec3D.dX(), vec3D.dY());
        }
    }

    private record Line2D(BigInteger A, BigInteger B, BigInteger C) {
        public Line2D(BigInteger x, BigInteger y, BigInteger dX, BigInteger dY) {
            // this(-dY, dX, dY * x - dX * y);
            this(dY.negate(), dX, dY.multiply(x).subtract(dX.multiply(y)));
        }

        public Line2D(Point2D p, Vec2D vec) {
            this(p.x(), p.y(), vec.dX(), vec.dY());
        }
    }

    private record Ray(Point3D center, Vec3D vec) {
    }

    private enum PLClass2D {
        ON, NEGATIVE, POSITIVE
    }

    private static BigInteger det2D(BigInteger a, BigInteger b, BigInteger c, BigInteger d) {
//        return a * d - b * c;
        return a.multiply(d).subtract(b.multiply(c));
    }

    private static BigInteger det3D(BigInteger a, BigInteger b, BigInteger c, BigInteger d, BigInteger e, BigInteger f, BigInteger g, BigInteger h, BigInteger i) {
        BigInteger detA = a.multiply(det2D(e, f, h, i));
        BigInteger detB = b.multiply(det2D(d, f, g, i));
        BigInteger detC = c.multiply(det2D(d, e, g, h));
        return detA.add(detB.negate()).add(detC);
    }

    private static PLClass2D getPLClass2D(Line2D line, Point2D point) {
        BigInteger d = line.A().multiply(point.x()).add(line.B().multiply(point.y())).add(line.C());
        return switch (d.signum()) {
            case 1 -> PLClass2D.POSITIVE;
            case 0 -> PLClass2D.ON;
            case -1 -> PLClass2D.NEGATIVE;
            default -> throw new IllegalStateException("Unexpected value: " + d.signum());
        };
    }

    private static boolean isIntersectionXYWithin(Ray ray1, Ray ray2, Pair<Long, Long> range) {
        long boxMinXY = range.getLeft();
        long boxMaxXY = range.getRight();

        Point2D c1 = new Point2D(ray1.center());
        Point2D c2 = new Point2D(ray2.center());
        Vec2D vec1 = new Vec2D(ray1.vec());
        Vec2D vec2 = new Vec2D(ray2.vec());
        Line2D line1 = new Line2D(c1, vec1);
        Line2D line2 = new Line2D(c2, vec2);
        BigInteger detXY = det2D(line1.A(), line2.A(), line1.B(), line2.B());
        if (detXY.signum() == 0) {
            // Parallel vectors, check if rays lie on the same line
            if (getPLClass2D(line1, c2) == PLClass2D.ON) {
                throw new NotImplementedException("2D vectors on the same line are not supported!");
            } else {
                return false;
            }
        } else {
            // Avoid division
            BigInteger interXTemp = det2D(line1.C(), line2.C(), line1.B(), line2.B()).negate(); // x = interXTemp / detXY
            BigInteger interYTemp = det2D(line1.A(), line2.A(), line1.C(), line2.C()).negate(); // y = interYTemp / detXY
            Range<BigInteger> rangeTemp = Range.of(
                    detXY.multiply(BigInteger.valueOf(boxMinXY)), detXY.multiply(BigInteger.valueOf(boxMaxXY))
            );

            // Check intersection within box
            if (rangeTemp.contains(interXTemp) && rangeTemp.contains(interYTemp)) {
                // check if both rays contain this point
                boolean isOnRay1 = interXTemp.compareTo((detXY.multiply(c1.x()))) * detXY.signum() * vec1.dX().signum() > 0
                                   || interYTemp.compareTo((detXY.multiply(c1.y()))) * detXY.signum() * vec1.dY().signum() > 0;
                boolean isOnRay2 = interXTemp.compareTo((detXY.multiply(c2.x()))) * detXY.signum() * vec2.dX().signum() > 0
                                   || interYTemp.compareTo((detXY.multiply(c2.y()))) * detXY.signum() * vec2.dY().signum() > 0;
                return isOnRay1 && isOnRay2;
            } else {
                return false;
            }
        }
    }

    private static boolean isParallel(Vec3D vec1, Vec3D vec2) {
        BigInteger a1 = vec1.dX(), a2 = vec1.dY(), a3 = vec1.dZ();
        BigInteger b1 = vec2.dX(), b2 = vec2.dY(), b3 = vec2.dZ();
        return det2D(a2, a3, b2, b3).signum() == 0 && det2D(a1, a3, b1, b3).signum() == 0 && det2D(a1, a2, b1, b2).signum() == 0;
    }

    private static boolean isIntersecting(Ray ray1, Ray ray2) {
        // | c2x-c1x   c2y-c1y   c2z-c1z |
        // |   d1x       d1y       d1z   |
        // |   d2x       d2y       d2z   |

        Point3D c1 = ray1.center();
        Point3D c2 = ray2.center();
        Vec3D v1 = ray1.vec();
        Vec3D v2 = ray2.vec();

        BigInteger det = det3D(
                c2.x().subtract(c1.x()), c2.y().subtract(c1.y()), c2.z().subtract(c1.z()),
                v1.dX(), v1.dY(), v1.dZ(),
                v2.dX(), v2.dY(), v2.dZ()
        );

        return det.signum() == 0;
    }

    private static boolean isSkew(Ray ray1, Ray ray2) {
        return !isIntersecting(ray1, ray2) || !isParallel(ray1.vec(), ray2.vec());
    }

    private static BoolExpr mkEquation(Context ctx, IntExpr p, IntExpr v, IntExpr t, IntNum cRay, IntNum vRay) {
        // p + v * t = cRay + vRay * t
        return ctx.mkEq(ctx.mkAdd(p, ctx.mkMul(v, t)), ctx.mkAdd(cRay, ctx.mkMul(vRay, t)));
    }

    private static void addRayEquations(Context ctx, Solver solver, IntExpr3D p, IntExpr3D v, IntExpr t, Ray ray) {
        IntNum cx = ctx.mkInt(ray.center.x().longValueExact());
        IntNum cy = ctx.mkInt(ray.center.y().longValueExact());
        IntNum cz = ctx.mkInt(ray.center.z().longValueExact());

        IntNum vx = ctx.mkInt(ray.vec().dX().longValueExact());
        IntNum vy = ctx.mkInt(ray.vec().dY().longValueExact());
        IntNum vz = ctx.mkInt(ray.vec().dZ().longValueExact());

        // p[xyz] + v[xyz] * t = ray1_p.[xyz] + ray1_v.[xyz] * t
        BoolExpr eqX1 = mkEquation(ctx, p.x(), v.x(), t, cx, vx);
        BoolExpr eqY1 = mkEquation(ctx, p.y(), v.y(), t, cy, vy);
        BoolExpr eqZ1 = mkEquation(ctx, p.z(), v.z(), t, cz, vz);

        solver.add(eqX1, eqY1, eqZ1);
    }

    private static BigInteger extractIntValue(Model model, IntExpr var) {
        Expr<IntSort> val = model.eval(var, false);
        if (val instanceof IntNum intVal) {
            return intVal.getBigInteger();
        }
        throw new IllegalStateException();
    }

    private static Point3D solve(Ray ray1, Ray ray2, Ray ray3) {
        try (Context ctx = new Context()) {
            // p[xyz] + v[xyz] * t1 = ray1_p.[xyz] + ray1_v.[xyz] * t1
            // p[xyz] + v[xyz] * t2 = ray2_p.[xyz] + ray2_v.[xyz] * t2
            // p[xyz] + v[xyz] * t3 = ray3_p.[xyz] + ray3_v.[xyz] * t3

            IntExpr px = ctx.mkIntConst("px");
            IntExpr py = ctx.mkIntConst("py");
            IntExpr pz = ctx.mkIntConst("pz");

            IntExpr vx = ctx.mkIntConst("vx");
            IntExpr vy = ctx.mkIntConst("vy");
            IntExpr vz = ctx.mkIntConst("vz");

            IntExpr t1 = ctx.mkIntConst("t1");
            IntExpr t2 = ctx.mkIntConst("t2");
            IntExpr t3 = ctx.mkIntConst("t3");

            IntExpr3D p = new IntExpr3D(px, py, pz);
            IntExpr3D v = new IntExpr3D(vx, vy, vz);

            Solver solver = ctx.mkSimpleSolver();

            addRayEquations(ctx, solver, p, v, t1, ray1);
            addRayEquations(ctx, solver, p, v, t2, ray2);
            addRayEquations(ctx, solver, p, v, t3, ray3);

            if (solver.check() != Status.SATISFIABLE) {
                throw new IllegalStateException("Could not solve");
            }

            Model model = solver.getModel();

            return new Point3D(extractIntValue(model, px), extractIntValue(model, py), extractIntValue(model, pz));
        }
    }

    public static TaskSolution solve(String input, Pair<Long, Long> range) {
        List<Ray> rays = input.lines()
                .map(line -> {
                    List<BigInteger> l = Arrays.stream(StringUtils.split(line, " ,@"))
                            .map(BigInteger::new)
                            .toList();
                    Point3D center = new Point3D(l.get(0), l.get(1), l.get(2));
                    Vec3D vec = new Vec3D(l.get(3), l.get(4), l.get(5));
                    return new Ray(center, vec);
                })
                .toList();

        EntryStream.ofPairs(rays)
                .forKeyValue((ray1, ray2) -> {
                    boolean isParallel = isParallel(ray1.vec(), ray2.vec());
                    boolean isSkew = isSkew(ray1, ray2);
                    if (isParallel) {
                        System.out.printf("WARNING: Parallel rays %s and %s\n", ray1, ray2);
                    }
                    if (!isSkew) {
                        System.out.printf("WARNING: Non-skew rays %s and %s\n", ray1, ray2);
                    }
                });

        long result1 = EntryStream.ofPairs(rays)
                .filterKeyValue((ray1, ray2) -> isIntersectionXYWithin(ray1, ray2, range))
                .count();

        // Get any 3 skew lines (needed for test input)
        // We assume the input is correct and pick any triple without further checks
        Triple<Ray, Ray, Ray> skewTriple = StreamEx.ofCombinations(rays.size(), 3)
                .map(indices -> Triple.of(rays.get(indices[0]), rays.get(indices[1]), rays.get(indices[2])))
                .filter(triple -> {
                    Ray ray1 = triple.getLeft();
                    Ray ray2 = triple.getMiddle();
                    Ray ray3 = triple.getRight();
                    return isSkew(ray1, ray2) && isSkew(ray2, ray3) && isSkew(ray3, ray1);
                })
                .findFirst().orElseThrow();

        // Obtain solution with z3 solver
        Point3D shootingPoint = solve(skewTriple.getLeft(), skewTriple.getMiddle(), skewTriple.getRight());
        long result2 = shootingPoint.x().add(shootingPoint.y()).add(shootingPoint.z()).longValueExact();

        return TaskSolution.of(result1, result2);
    }

    public static void main(String[] args) {
        Instant start2 = Instant.now();
        TaskSolution resultsTest = solve(TEST.input(), RANGE_TEST);
        System.out.printf("Time: %d ms\n", Duration.between(start2, Instant.now()).toMillis());
        Helpers.printResults(TEST, resultsTest);

        Instant start1 = Instant.now();
        TaskSolution resultsMain = solve(MAIN.input(), RANGE_MAIN);
        System.out.printf("Time: %d ms\n", Duration.between(start1, Instant.now()).toMillis());
        Helpers.printResults(MAIN, resultsMain);
    }
}
