bind_socket = "0.0.0.0:{{SERVICE.ANTI_SPAM.PORTS.WORKER}}";
milter = yes;
timeout = 120s;
upstream "local" {
  default = yes;
  self_scan = yes;
}