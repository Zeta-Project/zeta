![Build Container](https://github.com/Zeta-Project/zeta/workflows/Build%20Container/badge.svg) ![Test Services](https://github.com/Zeta-Project/zeta/workflows/Test%20Services/badge.svg) ![Codecoverage codecov.io](https://codecov.io/gh/Zeta-Project/zeta/branch/dev/graph/badge.svg)

# Zeta
<img src="https://avatars3.githubusercontent.com/u/29041085?s=150&v=4" alt="Snowplow logo" title="Snowplow" align="right" />

Zeta is a tool for automatically generating a graphical DSL from a range of text DSLs. In combination with a suitable metamodel, you render these text definitions for a generator that creates a graphical editor for the web. So it can be seen as a model-driven generation of graphical editors with the goal to generate simulations from a graphical DSL.

The Zeta Project was orginally developed by Markus Erhart under the direction of Marko Boger at the HTWG Konstanz. It is based on the previous developed open source project Modigen which presents a set of specially developed textual DSLs to fully describe graphical DSLs based on node and edge diagrams. Zeta continues to be actively developed by students of the HTWG Konstanz.

The basic structure is given by the DSLs which were defined specifically for the creation of graphical DSLs.

1. **[Concept](https://github.com/Zeta-Project/zeta/wiki/Concept-DSL)** - The graphical DSL is also called metamodel. With this DSL the underlying data structure for the later model instance can be defined.
2. **[Shape](https://github.com/Zeta-Project/zeta/wiki/Shape-DSL)** - The shape language is needed for the definition of forms of any complexity. This DSL offers different basic forms such as for example, rectangles, ellipses and polygons in any width and depth. An element can be build from any number and combination from the existing basic forms.
3. **[Style](https://github.com/Zeta-Project/zeta/wiki/Style-DSL)** - The style language provides the functionality for defining different representations (layouts or design features) for elements. This could be seen similar to cascading style sheets (CSS).
4. **[Diagram](https://github.com/Zeta-Project/zeta/wiki/Diagram-DSL)** - The diagram language is the base DSL, which is sufficient for very simple domain-specific graphical editors. It defines the mapping of simple shapes, styles, and the behavior of elements to metamodel classes.

## Find out more

| **[Technical Docs][technicalDocs]**     | **[Setup Guide][setup]**      | **[About Zeta][aboutZeta]**           |
|-----------------------------------------|-------------------------------|---------------------------------------|
| [![i1][techdocs-image]][technicalDocs]  | [![i2][setup-image]][setup]   | [![i3][roadmap-image]][aboutZeta]     |

Model-driven generation of graphical editors. The goal is to generate simulations from a graphical DSL.

## Example of Shape DSL, Style DSL, Diagram DSL and Concept Editor
The graphical editor is configured via the textual DSLs by using the DSL editor. The DSL editor supports auto-completion of keywords and the detection of possible parsing errors.

![DSL-Editor-Edit](https://github.com/Zeta-Project/zeta/blob/dev/api/wiki/ConceptEditor_DSLs.gif)

## Model Editor
With the graphical editor configured by the DSLs, you can create and edit any model you want.

![Model-Editor](https://github.com/Zeta-Project/zeta/blob/dev/api/wiki/Model-Editor-zeta.gif)

[techdocs-image]: https://d3i6fms1cm1j0i.cloudfront.net/github/images/techdocs.png
[setup-image]: https://d3i6fms1cm1j0i.cloudfront.net/github/images/setup.png
[roadmap-image]: https://d3i6fms1cm1j0i.cloudfront.net/github/images/roadmap.png

[aboutZeta]: https://github.com/Zeta-Project/zeta/wiki
[setup]: https://github.com/Zeta-Project/zeta/wiki/Installation
[technicalDocs]: https://github.com/Zeta-Project/zeta/wiki/Table-of-Contents
