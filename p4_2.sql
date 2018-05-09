-- CS457 PA4

-- This script includes the commands to be executed by two processes, P1 and P2

-- On P2:
USE CS457_PA4;
select * from Flights;
begin transaction;
update flights set status = 1 where seat = 22;
commit; --there should be nothing to commit; it's an "abort"
select * from Flights;

-- On P2:
select * from Flights;

---------------------
-- Expected output --
---------------------

-- On P2:
-- Using database CS457_PA4.
-- seat int|status int
-- 22|0
-- 23|1
-- Transaction starts.
-- Error: Table Flights is locked!
-- Transaction abort.
-- seat int|status int
-- 22|0
-- 23|1
-- seat int|status int
-- 22|1
-- 23|1