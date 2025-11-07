
package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.requestDTO.AdminRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    public String registerAdmin(AdminRequestDTO dto) {
        // ✅ COMPLETELY DISABLED: Admin registration through API
        throw new UnsupportedOperationException(
                "❌ Admin registration is disabled. Admins can only be created during application startup."
        );
    }
}
