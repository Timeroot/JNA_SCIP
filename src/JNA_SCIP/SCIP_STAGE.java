package JNA_SCIP;

/* type_set.h */
public enum SCIP_STAGE
{
   INIT,
   PROBLEM,
   TRANSFORMING,
   TRANSFORMED,
   INITPRESOLVE,
   PRESOLVING,
   EXITPRESOLVE,
   PRESOLVED,
   INITSOLVE,
   SOLVING,
   SOLVED,
   EXITSOLVE,
   FREETRANS,
   FREE
}