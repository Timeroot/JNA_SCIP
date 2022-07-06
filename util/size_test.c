#include <stdio.h>
#include <stdbool.h>

#include <scip/scip.h>
#include <scip/struct_var.h>
#include <scip/struct_cons.h>

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
	PRINT_SIZE(SCIP_Var)
	PRINT_SIZE(SCIP_Cons)
	return 0;
}
