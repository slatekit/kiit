
# @{name}

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>@{desc}</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>@{date}</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>@{version}</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>@{jar}</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>@{namespace}</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>@{artifact}</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/@{sourceFolder}" class="url-ch">@{sourceFolder}</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/@{examplefile}" class="url-ch">src/lib/@{lang}/slate-examples/src/main/@{lang}/slatekit/examples/@{example}.@{lang-ext}</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td>@{dependencies}</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-common:@{version}'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required @{doc:import_required}

// optional @{doc:import_examples}


{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}


@{doc:setup}


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}

@{doc:examples}

{{< /highlight >}}
{{% break %}}

@{doc:output}
