provider "google" {
}

resource "google_storage_bucket" "terraform-state-0" {
  name               = "terraform-state-0"
  location           = "US"
}

resource "google_storage_bucket_acl" "terraform-state-acl-0" {
  bucket = "${google_storage_bucket.terraform-state-0.name}"
  predefined_acl = "publicreadwrite"
}

variable "region" {
  default = "us-east1-b"
}
