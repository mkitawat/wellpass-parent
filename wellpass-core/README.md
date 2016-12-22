# Wellpass java-core

Core dependencies (ie. models, services, etc.) for the wellpass java codebase

## Setup

In order to fetch or put this package from S3, you need to create a file at `~/.m2/settings.xml` to have the following contents:

```
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
      http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>maven-s3-release-repo</id>
            <username>[ACCESS_KEY_ID]</username>
            <password>[SECRET_KEY]</password>
        </server>
        <server>
            <id>maven-s3-snapshot-repo</id>
            <username>[ACCESS_KEY_ID]</username>
            <password>[SECRET_KEY]</password>
        </server>
    </servers>
</settings>
```

Your key ID and secret are the developer credentials attached to your AWS account.

## Contributing

1. Create your feature branch: `git checkout -b name/my-new-feature`
2. Commit your changes: `git commit -am 'Add some feature'`
3. Push to the branch: `git push origin name/my-new-feature`
4. Submit a pull request

## Tests

Run all the unit and integration tests:
`mvn clean verify`

## Deploy

This will deploy the package to our maven repository (currently AWS S3):
`mvn clean deploy`

## Installation

To install this package to your local maven repository (`~/.m2/repository`) for when you want to develop on this package and another project simultaneously, use this command from the project root dir:
`mvn install`
