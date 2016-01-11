java -jar ./modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate \
  -i /Users/davidsolberg/Dropbox/iBucket_swagger/api/swagger/swagger.yaml \
  -l swift \
  -t ./modules/swagger-codegen/src/main/resources/swift \
  -o /Users/davidsolberg/Desktop/generated/