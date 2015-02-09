# sbt-webpack
An sbt-web plugin for the webpack module bundler.

## Current Status
I'm sad to say that at the moment, plugin does not yet work correctly. I'm still working through several major issues. I'd love to get advice and/or pull requests to adress the following:
 1. The plugin gets invoked 2x for every time a file has changed.
 2. The when webpack's output gets moved by SBT into the `/public` folder. everything gets flattened out for some reason. For example, the file `a/b/c.js` becomes a folder names `a` hanging off the root, a folder named `b` hanging off the root, and a file named `c.js` hanging off the root.
 3. While webpack itself exists in a webjar, very few of its dependencies do, so it must be installed via Node. See [this thread](https://groups.google.com/forum/#!topic/play-framework/m2X8NQFk5bk) for a little more of the backstory.
 
## Other TODOs
 * Write unit tests
 * Better (i.e. some) error handling. 
