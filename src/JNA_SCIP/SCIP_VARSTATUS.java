package JNA_SCIP;

/* type_var.h */
public enum SCIP_VARSTATUS {
   ORIGINAL ,
   LOOSE,
   COLUMN,
   FIXED,
   AGGREGATED,
   MULTAGGR,
   NEGATED,
}