// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "thermal",
    platforms: [
        .iOS("11.0")
    ],
    products: [
        .library(name: "thermal", targets: ["thermal"])
    ],
    dependencies: [
        .package(name: "FlutterFramework", path: "../FlutterFramework")
    ],
    targets: [
        .target(
            name: "thermal",
            dependencies: [
                .product(name: "FlutterFramework", package: "FlutterFramework")
            ],
            resources: [
                .process("PrivacyInfo.xcprivacy")
            ]
        )
    ]
)
