package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.mappers.OrganizationMapper;
import top.rrricardo.postcalendarbackend.models.Organization;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/organization")
public class OrganizationController extends ControllerBase {


    private final OrganizationMapper organizationMapper;


    public OrganizationController(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<Organization>>> getOrganizations(){
        var organizations = organizationMapper.getOrganizations();

        return ok(organizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Organization>> getOrganization(@PathVariable(value = "id") int id) {
        var organization = organizationMapper.getOrganizationById(id);

        if (organization == null) {
            return notFound("组织不存在");
        }

        return ok(organization);
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDTO<Organization>> createOrganization(@RequestBody Organization organization){

        organizationMapper.createOrganization(organization);

        return created();

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Organization>> updateOrganization
            (@PathVariable (value = "id") int id, @RequestBody Organization organization) throws NullPointerException{

        if (id != organization.getId()) {
            return badRequest();
        }

        var oldOrganization = getOrganization(id);
        if(oldOrganization == null){
            //组织不存在
            return notFound("组织不存在");
        }

        organizationMapper.updateOrganization(organization);

        var newOrganization = organizationMapper.getOrganizationById(id);

        if(newOrganization == null){
            throw new NullPointerException();
        }

        return ok(organization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Organization>> deleteOrganization(@PathVariable(value = "id") int id){

        var organization = organizationMapper.getOrganizationById(id);

        if (organization == null) {
            return notFound("组织不存在");
        }

        organizationMapper.deleteOrganization(id);

        return noContent();
    }
}
