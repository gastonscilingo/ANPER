#ANPER : Automatic NullPointerException Repairer

##What is _ANPER_?

ANPER is an automated tool to fix exceptions of the type _NullPointerException_ thrown while running a set of _JUnit_ tests.

##What kind of fixes _ANPER_ generates?

_ANPER_ uses code transformation to, given a line to repair, transform that line in several statements that wrap the original statements with a _try {...} catch (NullPointerException e) {...}_ statement. The library used by _ANPER_ to transform (or mutate) code is [muJava++](https://github.com/saiema/MuJava), an extension of the original [muJava](https://github.com/tradi/MuJava).

_ANPER_ currently supports fixes for _while_, _for_, _return_ and expression statements (method calls, assignments, unary expressions, etc).

###While statements

Given a _while_ statement of the form _while condition body_, _ANPER_ will transform that statement to:

    boolean c = false;
    try {c = CONDITION;}
    catch (NullPointerException npe)
    {System.out.println("NPE detected evaluating expression before while");}
    while(c) {
        BODY
        try {
            c = CONDITION
        } catch (NullPointerException npe) {
            System.out.println("NPE detected evaluating expression inside while");
            c = false;
        }
    }

###For statements

Given a _for_ statement of the form _for (initializations; condition; increments) body_, _ANPER_ will transform that statement to:

    boolean initOk = true;
    try {INITIALIZATIONS}
        catch (NullPointerException npe) {
        System.out.println("NPE detected in for initialization");
        initOk = false;
    }
    if (initOk) {
        boolean c = false;
        try {c = EXPRESSION;}
        catch (NullPointerException npe)
        {System.out.println("NPE detected before for condition evaluation");}
        while(c) {
            BODY
            try {INCREMENTS}
            catch (NullPointerException npe) {
                System.out.println("NPE detected in for increment");
                c = false;
            }
            try {c = EXPRESSION;}
            catch (NullPointerException npe) {
                System.out.println("NPE detected before for condition evaluation");
                c = false;
            }
        }
    }

###Return statements

Given a _return_ statement of the form _return expression_, _ANPER_ will transform that statement to:

    try {
        return EXPRESSION
    } catch (NullPointerException npe) {
        System.out.println("NPE detected");
        return [default value];
    }

Where _default value_ depends of the return type of the method in which the _return_ statement is.

###Expression statements

Given a _expression_ statement of the form _expression_, _ANPER_ will transform that statement to:

    try {
        EXPRESSION
    } catch (NullPointerException npe) {
        System.out.println("NPE detected");
    }

####InitOK and Cond Variables

These variables are created as _initOK\_RANDOM_ and _cond\_RANDOM_ where _RANDOM_ is a random string of size 10 using letters and digits. The random string allows to introduce new variables to the code with very little chance of name collision.

##How to use it

In the [releases](https://github.com/gastonscilingo/ANPER/releases) section you can find the last release of _ANPER_. There will be three files, _anper.jar_, _lib.zip_, and _default.properties_. _ANPER_ works by taking all arguments from a _.properties_ file (you can call _ANPER_ with a path to such file or, if you don't, _ANPER_ will search for a _default.properties_ on its calling directory.

To run _ANPER_ you must execute it using the following command:

_java -cp <**ANPER**>:<**LIB**>:<**CASE**>:<**TESTS**> anper.main.Anper [**PROPERTIES PATH**]_ where 

* **ANPER** is the path to _ANPER_'s jar.
* **LIB** is the path to the lib folder.
* **CASE** is the path to where the classes of the program you want to repair are. 
* **TESTS** is the path to where the classes of the _JUnit_ tests you want to use are. 
* **PROPERTIES PATH** is the path to a _.properties_ you want to use.

Any _.properties_ file you want to use must have the following lines (order is not important)

* **path.original.source= <PATH>** : the path to the source files of the program you want to repair, for example _src/_

* **path.original.bin= <PATH>** : the path to the class files of the program you want to repair, for example _bin/_

* **path.tests.bin= <PATH>** : the path to the class files of the _JUnit_ tests you want to use, for example _bin/_

* **path.mutants= <PATH>** : the path where the transformed code will be saved, for example _mutants/_

* **mutation.basic.tests= <QUALIFIED CLASS NAMES>** : the test classes you want to run, using fully qualified names like _a.b.C_ and separated by a space when you want to run more than one _JUnit_ test class.

* **mutation.advanced.allowedPackagesToReload=[PACKAGES]** : this is an optional argument that is used to improve the performance, since only one VM is used, mutated classes must be reloaded during runtime. But to properly link the reloaded mutated class, all related classes must also be reloaded. This argument tells the reloader to only reload classes inside the given packages. For example, in the given example, all classes involved are inside the _examples.foo_, if you wanted to add a _examples.foo2_ package with another example, the reloader will reload all classes inside the _test/_ folder each time tests are ran. To avoid this, you could put _examples.foo2_ in this argument and the reloader would only reload the classes in that package.

* **mutation.advanced.fullVerbose= [BOOLEAN]** : this is an optional argument that is used to define how much verbosity _ANPER_ will use.
