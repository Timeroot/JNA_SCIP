#include <stdio.h>
#include <stdbool.h>

#define SCIP_Real double
#define SCIP_VAR int
#define SCIP_CONS int
#define SCIP_PROP int

typedef struct SCIP_Hole SCIP_HOLE;               /**< hole in a domain of an integer variable */
struct SCIP_Hole
{
   SCIP_Real             left;               /**< left bound of open interval defining the hole (left,right) */
   SCIP_Real             right;              /**< right bound of open interval defining the hole (left,right) */
};

typedef struct SCIP_Holelist SCIP_HOLELIST;       /**< list of holes in a domain of an integer variable */
struct SCIP_Holelist
{
   SCIP_HOLE             hole;               /**< this hole */
   SCIP_HOLELIST*        next;               /**< next hole in list */
};

typedef struct SCIP_HoleChg SCIP_HOLECHG;         /**< changes in holelist of variables */
struct SCIP_HoleChg
{
   SCIP_HOLELIST**       ptr;                /**< changed list pointer */
   SCIP_HOLELIST*        newlist;            /**< new value of list pointer */
   SCIP_HOLELIST*        oldlist;            /**< old value of list pointer */
};

typedef struct SCIP_BranchingData SCIP_BRANCHINGDATA; /**< data for branching decision bound changes */
struct SCIP_BranchingData
{
   SCIP_Real             lpsolval;           /**< sol val of var in last LP prior to bound change, or SCIP_INVALID if unknown */
};

typedef struct SCIP_InferenceData SCIP_INFERENCEDATA; /**< data for inferred bound changes */
struct SCIP_InferenceData
{
   SCIP_VAR*             var;                /**< variable that was changed (parent of var, or var itself) */
   union
   {
      SCIP_CONS*         cons;               /**< constraint that infered this bound change, or NULL */
      SCIP_PROP*         prop;               /**< propagator that infered this bound change, or NULL */
   } reason;
   int                   info;               /**< user information for inference to help resolving the conflict */
};

typedef struct SCIP_BoundChg SCIP_BOUNDCHG;       /**< changes in bounds of variables */
struct SCIP_BoundChg
{
   SCIP_Real             newbound;           /**< new value for bound */
   union
   {
      SCIP_BRANCHINGDATA branchingdata;      /**< data for branching decisions */
      SCIP_INFERENCEDATA inferencedata;      /**< data for infered bound changes */
   } data;
   SCIP_VAR*             var;                /**< active variable to change the bounds for */
   unsigned int          boundchgtype:2;     /**< bound change type: branching decision or infered bound change */
   unsigned int          boundtype:1;        /**< type of bound for var: lower or upper bound */
   unsigned int          inferboundtype:1;   /**< type of bound for inference var (see inference data): lower or upper bound */
   unsigned int          applied:1;          /**< was this bound change applied at least once? */
   unsigned int          redundant:1;        /**< is this bound change redundant? */
};

typedef struct SCIP_BdChgIdx SCIP_BDCHGIDX;     /**< bound change information to track bound changes from root to current node */
struct SCIP_BdChgIdx
{
   int                   depth;              /**< depth of node where the bound change was created */
   int                   pos;                /**< position of bound change in node's domchg array */
};

struct SCIP_BdChgInfo
{
   SCIP_Real             oldbound;           /**< old value for bound */
   SCIP_Real             newbound;           /**< new value for bound */
   SCIP_VAR*             var;                /**< active variable that changed the bounds */
   SCIP_INFERENCEDATA    inferencedata;      /**< data for infered bound changes */
   SCIP_BDCHGIDX         bdchgidx;           /**< bound change index in path from root to current node */
   unsigned int          pos:27;             /**< position in the variable domain change array */
   unsigned int          boundchgtype:2;     /**< bound change type: branching decision or infered bound change */
   unsigned int          boundtype:1;        /**< type of bound for var: lower or upper bound */
   unsigned int          inferboundtype:1;   /**< type of bound for inference var (see inference data): lower or upper bound */
   unsigned int          redundant:1;        /**< does the bound change info belong to a redundant bound change? */
};

struct SCIP_DomChgBound
{
   unsigned int          nboundchgs:30;      /**< number of bound changes (must be first structure entry!) */
   unsigned int          domchgtype:2;       /**< type of domain change data (must be first structure entry!) */
   SCIP_BOUNDCHG*        boundchgs;          /**< array with changes in bounds of variables */
};

struct SCIP_DomChgBoth
{
   unsigned int          nboundchgs:30;      /**< number of bound changes (must be first structure entry!) */
   unsigned int          domchgtype:2;       /**< type of domain change data (must be first structure entry!) */
   SCIP_BOUNDCHG*        boundchgs;          /**< array with changes in bounds of variables */
   SCIP_HOLECHG*         holechgs;           /**< array with changes in hole lists */
   int                   nholechgs;          /**< number of hole list changes */
};

struct SCIP_DomChgDyn
{
   unsigned int          nboundchgs:30;      /**< number of bound changes (must be first structure entry!) */
   unsigned int          domchgtype:2;       /**< type of domain change data (must be first structure entry!) */
   SCIP_BOUNDCHG*        boundchgs;          /**< array with changes in bounds of variables */
   SCIP_HOLECHG*         holechgs;           /**< array with changes in hole lists */
   int                   nholechgs;          /**< number of hole list changes */
   int                   boundchgssize;      /**< size of bound changes array */
   int                   holechgssize;       /**< size of hole changes array */
};

#define PRINT_SIZE(datatype) {struct datatype Obj; printf("Size of " #datatype " = %zu\n", sizeof(Obj));}

int main(void)
{
	PRINT_SIZE(SCIP_Hole)
	PRINT_SIZE(SCIP_Holelist)
	PRINT_SIZE(SCIP_HoleChg)
	PRINT_SIZE(SCIP_BranchingData)
	PRINT_SIZE(SCIP_InferenceData)
	PRINT_SIZE(SCIP_BoundChg)
	PRINT_SIZE(SCIP_BdChgIdx)
	PRINT_SIZE(SCIP_BdChgInfo)
	return 0;
}
