package com.api;

import java.util.HashSet;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.api.model.Role;
import com.api.repository.RoleRepository;

@SpringBootApplication
public class CafeManagementSystemApplication {

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(CafeManagementSystemApplication.class, args);
	}

	@Bean
	CommandLineRunner run(){
		return (args) -> {
			Role admin = new Role(1L,"ROLE_ADMIN", "admin user", new HashSet<>());
			Role user = new Role(2L,"ROLE_USER", "user for only view", new HashSet<>());
			Role manager = new Role(3L,"ROLE_MANAGER", "manager for managing staff and product", new HashSet<>());
//			Role create = new Role(4L,"CREATE", "only create", new HashSet<>());
//			Role update = new Role(5L,"UPDATE", "only update", new HashSet<>());
//			Role delete = new Role(6L,"DELETE", "only delete", new HashSet<>());


			List<Role> roles = List.of(admin, user, manager);

			roleRepository.saveAll(roles);
		};
	}

}
