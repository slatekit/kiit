// swift-tools-version:5.5
import PackageDescription

let package = Package(
    name: "KiitCommonEnvs",
    platforms: [.iOS(.v13)],
    products: [
        .library(name: "KiitCommonEnvs", targets: ["KiitCommonEnvs"]),
    ],
    targets: [
        .binaryTarget(
            name: "KiitCommonEnvs",
            url: "https://github.com/slatekit/kiit/releases/download/v3.4.0/KiitCommonEnvs.xcframework.zip",
            checksum: "47d6f12719626619d1d900f665fb7569c358a3eb87dfe9f904348fa8e4804f1f"
        ),
    ]
)