package JNA_SCIP;

/* type_status.h */
public enum SCIP_STATUS {
    UNKNOWN,
    USERINTERRUPT,
    NODELIMIT,
    TOTALNODELIMIT,
    STALLNODELIMIT,
    TIMELIMIT,
    MEMLIMIT,
    GAPLIMIT,
    SOLLIMIT,
    BESTSOLLIMIT,
    RESTARTLIMIT,
    OPTIMAL,
    INFEASIBLE,
    UNBOUNDED,
    INFORUNBD,
    TERMINATE
 }