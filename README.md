
# JNA_SCIP

JNA\_SCIP is a set of Java bindings and wrappers to the [SCIP library](https://www.scipopt.org/)
, for *S*olving *C*onstraint *I*nteger *P*rograms. In this way it fills a
similar role to [JSCIPOpt](https://github.com/scipopt/JSCIPOpt), with the
differences that JNA\_SCIP aims to offer coverage of a much larger fraction
of the API, and that the initial author of JNA\_SCIP was unaware of JSCIPOpt
when initially developing it. :) JNA\_SCIP uses [JNA](https://github.com/java-native-access/jna)
for mapping native calls, as opposed to [SWIG](http://www.swig.org). It's
possible but unlikely this difference matters to you.

In particular, JNA\_SCIP offers support for custom constraint handlers,
heuristics, message handlers, and problem or variable callbacks. In our view,
these are the key things that take SCIP beyond a simple MILP solver into an
interactive optimization plugin framework.

# Using JNA_SCIP

If you want to learn how to use JNA\_SCIP, your best bet will be the examples
folder. There a few different styles to using JNA\_SCIP, depending if you
want to keep your code closer to C or write more in a Java style. To give a
brief idea:

```Java
SCIP scip = SCIP.create();
scip.includeDefaultPlugins();
scip.createProbBasic("test");

SCIP_VAR x = scip.createVarBasic("x", 0, 1.0, -40.0, SCIP_VARTYPE.INTEGER);
scip.addVar(x);

SCIP_VAR y = scip.createVarBasic("y", 0, 5.0, -30.0, SCIP_VARTYPE.CONTINUOUS);
scip.addVar(y);

SCIP_CONS cons_1 = scip.createConsBasicLinear("cons1", new SCIP_VAR[]{x,y}, new double[]{1,3}, 0, 12);
scip.addCons(cons_1);
```

To use JNA\_SCIP, you'll need to add JNA libraries to your class path, and you'll
need `libscip.so` where JNA can find it. Your working directory should work,
but you can also set the system property `jna.library.path`, or [a few other
options listed here](https://java-native-access.github.io/jna/4.2.1/com/sun/jna/NativeLibrary.html#library_search_paths).

## Examples

JNA\_SCIP currently has 5 example programs:
 * Simple\_MILP, which solves a two-variable MILP.
 * Queens, a translation of SCIP's n-Queens problem.
   * Queens_C is a 1-to-1 translation of the C.
   * Queens_Obj is showing how to refer to variable names in a somewhat more Java way.
 * Heuristic, which shows how to implement a SCIP_HEUR callback.
 * Conshdlr, which shows how to implement a constraint handler callback.
   * Conshdlr_Manual gives the callbacks to SCIP directly.
   * Conshdlr_Obj uses JNA\_SCIP's helper classes (ConstraintHandler) to make the management easier.
 * LOP, a translation of SCIP's Linear Ordering Problem solver. It includes a constraint
   handler and a problem reader, and uses JNA\_SCIP's ConstraintHandler structures.

## How to find a function

Alright, you have a SCIP function with a certain name, and you want to know
how to use it in JNA\_SCIP. Starting with a name `SCIPxxxx`, the rules are:

1. Is your function's first argument not a "SCIP object", like `SCIP*` or
`SCIP_VAR*`? Such as `SCIPmessagePrintError(char* fmt, args..)` which starts
with a plain-old string, or the no-arg `SCIPmessageSetErrorPrintingDefault()`.
You can find these (with the `SCIP` dropped from the name) in `JSCIP`, for example,
`JSCIP.messageSetErrorPrintingDefault()`.
2. Otherwise, your function will be accessible as a member function of that type:
  1. If the original function was `SCIPxxx(obj, args..)`, you'll find a member
    function named `obj.xxx(args..)`.
  2. One exception is if the object's type starts the name of the method, in
     which case it is dropped. For example, `SCIPvarGetName(SCIP_VAR v)`
     becomes `v.getName()`, not `v.varGetName()` (because that method name would
     be silly).
3. If your function isn't there, check that it's actually implemented -- many
things still are not! You can search the repo for the full method name and
if there's no hits then we haven't yet. Feel free to raise an issue (or better
a pull request).

If this doesn't answer your questions, you can read the next section, which
goes into more details of the C translation.

# Translating from C

Below offers details on various things have been translated from the C API.
Ideally most things can be found by simply grepping JNA\_SCIP for the
desired method name or looking at a couple of the examples; but if you're
doing something more intricate or looking to contribute, please keep reading.

## API Calls

Methods are generally available in a few ways of varying friendliness. This is
so that you can access C-style APIs if desired, but usually write cleaner Java
code. All of the C API calls are given in the class `JSCIP`, and have names
that start with "SCIP", e.g. `SCIPprintOrigProblem`. These are non-static
methods of the interface, and call be called via the `JSCIP.LIB` object, e.g.
`JSCIP.LIB.SCIPprintOrigProblem`.

Many of these `SCIPxx` methods have a return type `SCIP_RETCODE`, which
corresponds to the `SCIP_RETCODE` in the C API. It is standard practice in
using SCIP to wrap these methods in a `SCIP_CALL( )` macro, which checks the
retcode. For this purpose, JNA\_SCIP offers methods like
`CALL_SCIPprintOrigProblem`, which checks the retcode, throws an exception if
needed, and otherwise return void. These are static and call be called as
`JSCIP.CALL_SCIPprintOrigProblem`. These also take care of mapping pointers to
pointers for you. For instance, the C method `SCIPincludeHeurBasic` takes a
`SCIP_HEUR**` as its second argument, which then gets overwritten with the
newly created `SCIP_HEUR*` as a form of returned value. In Java this means that
`SCIPincludeHeurBasic` takes a `PointerByReference` as its second argument. The
cleaner `CALL_SCIPincludeHeurBasic` instead takes a `SCIP_HEUR` as its second
argument and sets the pointer inside to point to the new instance. As another
example, SCIPconstructLP takes a `bool*` which it uses to return a success value
separate from the `SCIP_RETCODE` it returns directly.

Further, many methods have a simpler name with no prefixes, and that will take
care of several kinds of extra type mapping for you. Sometimes the change is
trivial, for example `JSCIP.warningMessage` simply forwards all arguments to
`JSCIP.LIB.SCIPwarningMessage` -- and since `SCIPwarningMessage` doesn't return
a `SCIP_RETCODE`, that's all that happens. `JSCIP.getSols` takes the raw
pointer from `JSCIP.LIB.SCIPgetSols`, allocates an appropriately-sized array,
and returns a `SCIP_SOL[]`. Most methods that create a new object (like
`SCIPcreateVarBasic`, `SCIPincludeHeurBasic`) have a simplified name
(`createVarBasic`, `includeHeurBasic`) that return the new object. For example,
`CALL_SCIPincludeHeurBasic` on its own will set a caller-provided `SCIP_HEUR`
to point to the new object, and returns void, while `includeHeurBasic`
constructs, creates and returns a new `SCIP_HEUR` for you.

Finally, most methods are associated to some object, whichever is their first
argument. For instance, since the first argument of
`JSCIP.CALL_SCIPflushRowExtensions(SCIP scip, SCIP_ROW row)` is `SCIP`, you
can instead call `scip.flushRowExtensions(row)` on any `SCIP` object `scip`.
A significant fraction of the time, getters and setters on an object start with
the name of the object, in which case these are dropped. For instance,
`SCIPvarGetStatus` becomes `var.getStatus` on a `SCIP_VAR var`, and
`SCIPconsIsChecked` becomes `cons.isChecked` on ` SCIP_CONS cons`. Most methods
in the SCIP library take a `SCIP` object as their first argument, so most
methods are available on `SCIP` objects. Only a handful of methods can only
be called through the `JSCIP` index, such as
`JSCIP.messagePrintError(String fmt, Object... vals)`.

## Enums

The majority of enums in the C library have names of the form
`SCIP_enumtype_value`, for example the type `SCIP_BOUNDCHGTYPE` includes
`SCIP_BOUNDCHGTYPE_BRANCHING` and `SCIP_BOUNDCHGTYPE_CONSINFER`. In Java, each
enum is its own type, and the prefix is dropped: the `SCIP_BOUNDCHGTYPE` enum
has fields `BRANCHING` and `CONSINFER`. In practice this means that translating
from C means replacing one underscore with a period: `SCIP_BOUNDCHGTYPE.BRANCHING`.

A couple notable exceptions are `SCIP_RETCODE`, `SCIP_SETTING`, and `SCIP_RESULT`
which use the same names as in C, e.g. `SCIP_OKAY`, `SCIP_DIDNONTRUN`. The enum
`SCIP_PROPTIMING`, which was made for bitwise masking, has all 16 values defined:
`SCIP_PROPTIMING.BEFORELP_DURINGLPLOOP` or `SCIP_PROPTIMING.ALWAYS` for example.
`SCIP_HEURTIMING` would require 2048 combinations to list all combinations, so
instead the 15 recommended combinations are given and other can be combined by
manually choosing bits (e.g. `SCIP_HEURTIMING.of(0x3571)`).

## Callbacks

Callback types are all defined in the `SCIP_DECL_*` classes. Any method can be
provided that matches the functional signature, but beware of using Java lambdas:
calling `SCIPsetSomeCallback(scip, (x,y) -> foo(x))` will lead to crashes! The
lambda is created, mapped to a trampoline that C can call, and then when the
SCIP call returns Java sees no lingering references to the lambda and frees it.
When SCIP later tried to call your callback, it will land on zeroed out memory.
For this reason, you must save any lambdas you create in a place where they will
last until SCIP is done with them.

Of course, you're also free to not use lambdas, and define actual objects
implementing your `SCIP_DECL_CONSENFOLP` and so on. But beware that one object
should not implement more than one callback method: JNA is, unfortunately,
unable to distinguish which callback SCIP is asking for, so if one class
implements both `SCIP_DECL_CONSENFOLP` and `SCIP_DECL_CONSCHECK`, SCIP will
always call `SCIP_DECL_CONSENFOLP`, regardless of which type it's passed as.

JNA\_SCIP offers the `ConstraintHandler` class to handle most of these details,
and let you write sane callbacks. A worked example is given in `Conshdlr_Obj`,
while `Conshdlr_Manual` shows the manual way using native C types.

## File Pointers

Several SCIP functions take (or return) file pointers, `FILE*`. On most systems
these are actually pointers to integer file descriptors. JNA\_SCIP offers the
FILEPTR named pointer type for using these while avoiding type confusion. You
can always provide `null` to Java functions that take a `FILEPTR`, and this will
refer to printing to standard out. If you need SCIP to print somewhere else,
you will need to use some platform-specific native libraries to open streams, or
you can use `SCIPfopen` to open a file.
