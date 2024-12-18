package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.TitleDto;
import lap_english.dto.UserTitleDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.ITitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/title")
@RestController
@RequiredArgsConstructor
public class TitleController {
    private final ITitleService titleService;

    @Operation(summary = "nhận phần thưởng của title đã đạt được ", description = "trả về thông tin title đã nhận thưởng",
            tags = {"title"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, không trả về dữ liệu"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi do progress chưa đạt 100% hoặc đã nhận thưởng rồi ",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping("/claim-title/{titleId}")
    public ResponseData<UserTitleDto> claimTitle(@PathVariable Long titleId) {
        return new ResponseData<>(200, "success", titleService.claimTitle(titleId));
    }


    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<TitleDto> create(@Validated @RequestPart("data") TitleDto titleDto, @RequestPart(value = "file",required = false) MultipartFile file) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", titleService.create(titleDto,file));
    }

    @Operation(summary = "xoa task", description = "k tra ve gi ca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable Long id) {
        titleService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }


    @Operation(summary = "update", description = "Trả về đối tượng vừa update ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<TitleDto> update(@PathVariable Long id, @Validated @RequestBody TitleDto titleDto, @RequestParam(value = "file",required = false) MultipartFile file) {
        titleDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", titleService.update(titleDto,file));
    }
    @Operation(summary = "getPage title task ", description = "Trả về danh sach daily task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<TitleDto>>> getPage(Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", titleService.getAllTask(pageable));
    }
}
