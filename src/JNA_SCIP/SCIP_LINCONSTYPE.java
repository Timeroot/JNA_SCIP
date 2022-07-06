package JNA_SCIP;

/* type_cons.h */
public enum SCIP_LINCONSTYPE {
    EMPTY,
    FREE,
    SINGLETON,
    AGGREGATION,
    PRECEDENCE,
    VARBOUND,
    SETPARTITION,
    SETPACKING,
    SETCOVERING,
    CARDINALITY,
    INVKNAPSACK,
    EQKNAPSACK,
    BINPACKING,
    KNAPSACK,
    INTKNAPSACK,
    MIXEDBINARY,
    GENERAL
}