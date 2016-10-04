---
layout: start_page@{layout}
title: module @{name}
permalink: /mod-@{namelower}
---

# @{name}

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | @{desc} | 
| **date**| @{date} |
| **version** | @{version}  |
| **jar** | @{jar}  |
| **namespace** | @{namespace}  |
| **source core** | @{source}.scala  |
| **source folder** | [@{sourceFolder}](https://github.com/kishorereddy/blend-server/tree/master@{sourceFolder})  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate.examples/@{example}.scala](https://github.com/kishorereddy/blend-server/tree/master@{examplefile}) |
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