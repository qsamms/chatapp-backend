
.PHONY: run
run:
	. conf/set_env.sh && ./mvnw spring-boot:run

.PHONY: install
install:
	mvn dependency:resolve

.PHONY: test_local
test_local:
	. conf/set_env.sh && mvn test

.PHONY: pre-commit-install
pre-commit-install:
	cp conf/pre-commit .git/hooks/pre-commit
	chmod +x .git/hooks/pre-commit

.PHONY: build
build:
	docker build -t chatapp-backend .

.PHONY: run-docker
run-docker:
	docker stop backend postgres es || true
	docker rm backend postgres es || true
	. conf/set_env_docker.sh && docker-compose up -d
