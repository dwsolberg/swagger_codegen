java -jar ./modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate \
  -i /Users/davidsolberg/Dropbox/iBucket_swagger/api/swagger/swagger_v2.yaml \
  -l swift \
  -t ./modules/swagger-codegen/src/main/resources/swift \
  -o /Users/davidsolberg/Desktop/generated/

  java -jar ./modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate \
  -i /Users/davidsolberg/Dropbox/iBucket_swagger/api/swagger/swagger_v2.yaml \
  -l coredata \
  -t ./modules/swagger-codegen/src/main/resources/coredata \
  -o /Users/davidsolberg/Desktop/generated/

    java -jar ./modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate \
  -i /Users/davidsolberg/Dropbox/iBucket_swagger/api/swagger/swagger_v2.yaml \
  -l coredataclass \
  -t ./modules/swagger-codegen/src/main/resources/coredataclass \
  -o /Users/davidsolberg/Desktop/generated/