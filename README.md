# sbt-webpack
An sbt-web plugin for the webpack module bundler.

## Current Status
At the moment, the plugin does not yet work 100% correctly. I'm still working through a few major issues. I'd love to get advice and/or pull requests to address the following:
 1. The plugin gets invoked 2x for every time a file has changed.
 2. While webpack itself exists in a webjar, very few of its dependencies do, so it must be installed via Node. See [this thread](https://groups.google.com/forum/#!topic/play-framework/m2X8NQFk5bk) for a little more of the backstory.
 
## Other TODOs
 * Write unit tests
 * Better (i.e. some) error handling.

## License
`sbt-webpack` is licensed under the [Apache License, Version 2.0](https://github.com/HomeBay/sbt-webpack/blob/master/LICENSE)
