package com.example.webpack

case class Config(service: ServiceConfig)

case class ServiceConfig(dev: Boolean, port: Int, interface: String)
