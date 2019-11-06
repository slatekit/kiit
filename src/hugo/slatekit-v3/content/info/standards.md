---
title: "Standards"
date: 2019-03-17T13:02:30-04:00
section_header: Standards
---

# Overview
The overarching design in Slate Kit involves an emphasis on simplicity, reasonably light-weight and modular components that can be used both on the Server Side, and on the Client ( Android ). This is also designed to be much more of a collection of libraries than a full-fledged framework, in order to keep components decoupled and support using as little or as much of the code as possible. With regard to programming style, there is also a strong preference towards <strong>pragmatic functional programming</strong>, immutability, and use of higher-order functions. 
{{% break %}}

# Standards
Standard Kotlin endorsed coding standards are applied via IntelliJ, Ktlint, and editorconfigs. 
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>1</strong></td>
        <td><strong>Git</strong></td>
        <td><strong>One flow</strong> is used as the branching model</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong>Lint</strong></td>
        <td><strong>Ktlint</strong> is currently used to format the code in projects. 
However, this is not currently automated and/or part of gradle.
The linting process is done manually periodically as of now, but will later be aautomated.</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong>Settings</strong></td>
        <td>An <strong>.editorconfig</strong> is placed in all projects to enforce certain settings.</td>
    </tr>
</table>
{{% break %}}


# Philosophy
{{% sk-philosophy %}}
{{% break %}}


# Infrastucture
There are thin abstractions over some infrastructure services such as Files, queues, docs. Currently, only AWS implementations are available for the infrastructure abstractions. However, in the future, support for Google and Azure cloud services may be implemented. Other services are using directly.
Refer to {{% sk-link href="start/overview#tech" text="infrastructure" %}}
{{% break %}}


# Code
There is an emphasis towards <strong>pragmatic</strong> functional programming, without going towards a <strong>pure</strong> functional programming style. A heavy emphasis towards simplicity and readability over conciseness and cleverness. Generally speaking the following approaches are followed:

<table class="table table-bordered table-striped">
    <tr>
        <td><strong>1</strong></td>
        <td><strong>Immutablity</strong></td>
        <td>Use <strong>val</strong> over <strong>var</strong></td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong>Nulls</strong></td>
        <td>Avoid using <strong>!!</strong> on nullable types</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong>Errors</strong></td>
        <td>Prefer functional error-handling via the
            {{% sk-link-arch name="results" %}} type and its aliases <strong>Outcome, Try, Notice, Validated</strong> over exceptions.
        </td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong>Types</strong></td>
        <td>Prefer strong types/enums over "string" types</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong>ADT</strong></td>
        <td>Use Algebraic Data Types by leveraging <strong>sealed classes</strong> in conjuction with pattern-matching</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong>Concurrency</strong></td>
        <td>Use Kotlin Coroutines where applicable</td>
    </tr>
</table>
{{% break %}}