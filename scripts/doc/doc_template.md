---
layout: start_page@{layout}
title: module @{name}
permalink: /kotlin-mod-@{namelower}
---

# @{name}

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | @{desc} | 
| **date**| @{date} |
| **version** | @{version}  |
| **jar** | @{jar}  |
| **namespace** | @{namespace}  |
| **source core** | @{source}.@{lang-ext}  |
| **source folder** | [@{sourceFolder}](https://github.com/code-helix/slatekit/tree/master/@{sourceFolder})  |
| **example** | [/src/apps/@{lang}/slate-examples/src/main/@{lang}/slatekit/examples/@{example}.@{lang-ext}](https://github.com/code-helix/slatekit/tree/master/@{examplefile}) |
| **depends on** | @{dependencies}  |

## Import
```scala 
// required @{doc:import_required}

// optional @{doc:import_examples}

```

## Setup
```scala

@{doc:setup}

```

## Usage
```scala

@{doc:examples}

```

@{doc:output}