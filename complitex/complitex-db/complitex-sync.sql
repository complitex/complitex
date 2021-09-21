CREATE TABLE "domain_sync" (
  "id" BIGSERIAL PRIMARY KEY, --Идентификатор записи синхронизации
  "parent_id" BIGINT, --Внешний идентификатор родительского объекта
  "additional_parent_id" VARCHAR, --Внешний идентификатор дополнительного родительского объекта
  "external_id" BIGINT NOT NULL, --Внешний идентификатор
  "additional_external_id" VARCHAR, --Дополнительный внешний идентификатор
  "name" VARCHAR NOT NULL, --Название
  "additional_name" VARCHAR, --Дополнительное название
  "alt_name" VARCHAR, --Украинское название
  "alt_additional_name" VARCHAR, --Украинское дополнительное название
  "servicing_organization" VARCHAR, --Обслуживающая организация
  "balance_holder" VARCHAR, --Балансодержатель
  "type" INTEGER NOT NULL, --Тип синхронизации
  "status" INTEGER NOT NULL, --Статус синхронизации
  "status_detail" INTEGER, --Дополнительный статус синхронизации
  "date" TIMESTAMP NOT NULL --Дата актуальности
); --Синхронизация справочников

CREATE INDEX "domain_sync_parent_id_index" ON "domain_sync" ("parent_id");
CREATE INDEX "domain_sync_additional_parent_id_index" ON "domain_sync" ("additional_parent_id");
CREATE INDEX "domain_sync_external_id_index" ON "domain_sync" ("external_id");
CREATE INDEX "domain_sync_additional_external_id_index" ON "domain_sync" ("additional_external_id");
CREATE INDEX "domain_sync_servicing_organization_index" ON "domain_sync" ("servicing_organization");
CREATE INDEX "domain_sync_balance_holder_index" ON "domain_sync" ("balance_holder");
CREATE INDEX "domain_sync_type_index" ON "domain_sync" ("type");
CREATE INDEX "domain_sync_status_index" ON "domain_sync" ("status");
CREATE INDEX "domain_sync_date_index" ON "domain_sync" ("date");
