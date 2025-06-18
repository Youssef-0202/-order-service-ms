ALTER TABLE t_orders
ADD CONSTRAINT uq_order_num unique (order_number);