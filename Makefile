
.PHONY: run
run:
	. conf/set_env.sh && ./mvnw spring-boot:run

.PHONY: install
install:
	mvn clean install -DskipTests

.PHONY: test_local
test_local:
	. conf/set_env.sh && mvn test