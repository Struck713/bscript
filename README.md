# bScript
This is just my silly attempt at building an interpreter in Java.
I eventually want to support a full set of grammar but right now it only includes the following:
- ADD (+)
- SUBTRACT (-)
- DIVIDE (/)
- MULTIPLY (*)
- NUMBER (ℝ)
- IDENTIFIER (any non-reserved string)
- TRUE
- FALSE
- EOF (special end-of-file type)

I plan to use this project in some other projects (that have a need for simple interpreted scripts,
so eventually this language should be able to make native calls to Java functions.