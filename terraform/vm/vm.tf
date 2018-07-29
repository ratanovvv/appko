
resource "google_compute_instance" "default" {
  name         = "test0"
  machine_type = "n1-standard-1"
  zone         = "${var.region}"

  tags = ["foo", "bar"]

  boot_disk {
    initialize_params {
      image = "centos-7"
      size = 10
      type = "pd-ssd"
    }
  }

  // Local SSD disk
  scratch_disk {
  }

  network_interface {
    network = "default"
    address = "10.142.0.25"

    access_config {
      // Ephemeral IP
    }
  }

  metadata {
    ssh-keys = "rvv:${file("${var.ssh_key}")}"
  }

  metadata_startup_script = "echo hi > /test.txt"

  service_account {
    scopes = ["userinfo-email", "compute-ro", "storage-ro"]
  }
}
