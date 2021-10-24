#!/bin/sh
export JDBC_DRIVER="org.postgresql.Driver"
export JDBC_URL="jdbc:postgresql://localhost:5432/exec_module"
export POSTGRES_USERNAME="exec_module"
export POSTGRES_PASSWORD="exec_module1010#"
export WORKING_DIR="/home/execution-module/"
export TERRAFORM_POSTGRES_CONN_STR="postgres://exec_module1:exec_module1010@localhost:5432/exec_module?sslmode=disable"
export PULUMI_BASE_URL="http://128.199.17.156:6792"