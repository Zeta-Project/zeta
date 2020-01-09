[![Build Status](https://travis-ci.org/Zeta-Project/zeta.svg?branch=dev)](https://travis-ci.org/Zeta-Project/zeta)
[![Codecoverage codecov.io](https://codecov.io/gh/Zeta-Project/zeta/branch/dev/graph/badge.svg)](https://codecov.io/gh/Zeta-Project/zeta)
# Zeta
<img src="https://avatars3.githubusercontent.com/u/29041085?s=150&v=4" alt="Snowplow logo" title="Snowplow" align="right" />

Zeta is a tool for automatically generating a graphical DSL from a range of text DSLs. In combination with a suitable meta-model, you render these text definitions for a generator that creates a graphical editor for the web. So it can bee seen as a Model-driven generation of graphical editors with the goal to generate simulations from a graphical DSL.

The Zeta Project was orginally developed by Markus Erhart under the direction of Marco Boger at the University of Konstanz. It is based on the previous developed open source project Modigen which presents a set of specially developed textual DSLs to fully describe graphical DSLs based on node and edge diagrams. Zeta continues to be actively developed by students of the University of Konstanz.

The basic structure is given by the DSLs which were defined specifically for the creation of graphical DSLs.

1. **Diagram** - The Diagram language is the base DSL, which is sufficient for very simple domain-specific graphical editors. It defines the mapping of simple shapes, styles, and the behavior of elements to meta-model classes.
2. **Shape** - The Shape language is needed for the definition of forms of any complexity. This DSL offers different basic forms such as for example, rectangles, ellipses and polygons in any width and depth. An element can be biuld from any number and combination from the existing basic forms.
3. **Style** - The style language provides the functionality for defining different representations (layouts or design features) for elements. This could be seen similar to cascading style sheets (CSS).
4. **Concept** - The graphical DSL is also called Meta Model. With this DSL the underlying data structure for the later model instance can be defined.


The structure of the architecture is shown in the following figure.
<img src="webapp/assets/ZetaArchitecture.PNG" alt="Snowplow logo" title="Snowplow" align="right" />


## Find out more

| **[Technical Docs][aboutZeta]**     | **[Setup Guide][setup]**     | **[About Zeta][aboutZeta]**           | **[Contributing][aboutZeta]**           |
|-------------------------------------|-------------------------------|-----------------------------------|---------------------------------------------|
| [![i1][techdocs-image]][aboutZeta] | [![i2][setup-image]][setup] | [![i3][roadmap-image]][aboutZeta] | [![i4][contributing-image]][aboutZeta] |

Model-driven generation of graphical editors. <br>
The goal is to generate simulations from a graphical DSL.

## DSL-Editor
The Graphical-Editor is configured via the textual DSL's by using the DSL-Editor.
<br>
The DSL-Editor supports auto-completion of keywords and the detection of possible parsing errors. 
![DSL-Editor-Edit](https://github.com/Zeta-Project/zeta/blob/dev/api/wiki/dsl-editor.gif)

## Outline
To keep the overview, you can use the outline function, which displays all defined elements of the DSL and 
<br>
allows to select them within the DSL editor.
![DSL-Editor-Outline](https://github.com/Zeta-Project/zeta/blob/dev/api/wiki/dsl-editor-2.gif)

## Model-Editor
With the graphical editor configured by the DSL's, you can create and edit any model you want.
![Model-Editor](https://github.com/Zeta-Project/zeta/blob/dev/api/wiki/model-editor.gif)

## Code-Generator
Now scala code can be generated and downloaded from a previously created and saved model.
![Code-Generator](https://github.com/Zeta-Project/zeta/blob/dev/api/wiki/code-generator.gif)

![](https://avatars3.githubusercontent.com/u/29041085?s=150&v=4)

[techdocs-image]: https://d3i6fms1cm1j0i.cloudfront.net/github/images/techdocs.png
[setup-image]: https://d3i6fms1cm1j0i.cloudfront.net/github/images/setup.png
[roadmap-image]: https://d3i6fms1cm1j0i.cloudfront.net/github/images/roadmap.png
[contributing-image]: https://d3i6fms1cm1j0i.cloudfront.net/github/images/contributing.png

[aboutZeta]: https://github.com/Zeta-Project/zeta/wiki
[setup]: https://github.com/Zeta-Project/zeta/wiki/Installation
