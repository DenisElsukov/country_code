\connect postgres

SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'countrycode';

DROP DATABASE IF EXISTS countrycode;

DROP USER IF EXISTS "user";