# S&F Software Framework

This is the Java framework S&F Software is currently utilizing to create our games. While this repository **IS** public, we do not encourage anyone to actually use this framework for your own games.

## Game Project Setup

Outside of referencing the framework project in your own game project, you **must** provide a path to the LWJGL native runtimes or you will receive a nasty exception on startup. 

For example, in Eclipse:
- _Run_ -> _Run Configurations_ -> _Java Application_ -> _Arguments_ Tab
- Add the following to _VM arguments_: ```-Djava.library.path={framework_path}/target/natives```
