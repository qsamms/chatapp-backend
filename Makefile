
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

.PHONY: build-postgres
build-postgres:
	. conf/set_env_docker.sh && docker build -f conf/postgres/Dockerfile \
	--build-arg PG_USER=$(DB_USER) \
	--build-arg PG_PASSWORD=$(DB_PASSWORD) \
	--build-arg PG_DB=$(DB_NAME) \-t chatapp-postgres .

.PHONY: run-docker
run-docker:
	docker stop backend postgres || true
	docker rm backend postgres || true
	. conf/set_env_docker.sh && docker-compose up -d
