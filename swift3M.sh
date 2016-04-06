java -jar ./modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate \
  -i /Users/davidsolberg/Dropbox/WhittenMobile/Active\ Projects/3M/3M_Swagger_Repo/api/swagger/swagger.yaml \
  -l swift \
  -t ./modules/swagger-codegen/src/main/resources/swift \
  -o /Users/davidsolberg/Desktop/3m_generated/