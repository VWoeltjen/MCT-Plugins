MCT-Plugins
===========

Each plug-in may be built in one of two ways. If building MCT from source, these may be included alongside other projects and packaged by making appropriate changes to pom.xml within an assembly. As a convenience, ant scripts are included to build as stand-alone plugins. An existing platform build (such as the evaluation version) is required and may be specified as a property "mct.dir", i.e.:

ant -Dmct.dir=/Applications/MCT


Ancestor View:

A plug-in for viewing a graph of referencing components within MCT.


Chronology:

A set of interfaces used for communicating time-stamped information between plugins.


Notebook:

A plug-in for making and maintaining notes within MCT. Notes may be annotated with other objects, such as telemetry elements, by dragging and dropping them into the note's text field.


Timeline:

A plug-in for viewing time-stamped information (notes, events) in a graphical timeline. Depends upon Chronology.


Earth View:

A plug-in for viewing state vectors relative to the Earth. To view, create an "Orbit" object from the Create menu. You may set initial vectors (units are km and km/s respectively, and position is relative to Earth's center; orbits are approximated at an accelerated rate and are not physically accurate). 

Contains a true-color image of the Earth, owned by NASA, from the Visible Earth catalog. 

http://visibleearth.nasa.gov/view.php?id=73909

R. Stockli, E. Vermote, N. Saleous, R. Simmon and D. Herring (2005). The Blue Marble Next Generation - A true color earth dataset including seasonal dynamics from MODIS. Published by the NASA Earth Observatory. Corresponding author: rstockli@climate.gsfc.nasa.gov