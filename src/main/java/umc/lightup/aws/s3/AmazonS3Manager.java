package umc.lightup.aws.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.common.Uuid;
import umc.lightup.common.repository.UuidRepository;
import umc.lightup.config.AmazonConfig;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public void deleteFile(String imageUrl) {
        String keyName = extractKeyNameFromUrl(imageUrl);

        try {
            amazonS3.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), keyName));
            log.info("Deleted file from S3: {}", keyName);
        } catch (AmazonServiceException e) {
            log.error("AWS error while deleting file: {}", e.getErrorMessage());
            throw e;
        } catch (SdkClientException e) {
            log.error("Client error while deleting file: {}", e.getMessage());
            throw e;
        }
    }

    private String extractKeyNameFromUrl(String url) {
        return url.substring(url.indexOf(".com/") + 5);
    }

    public String generateItemProfileImageKeyName(Uuid uuid) {
        return amazonConfig.getItemProfileImagePath() + '/' + uuid.getUuid();
    }

    public String generateItemFileKeyName(Uuid uuid) {
        return amazonConfig.getItemFilePath() + '/' + uuid.getUuid();
    }

    public String generateProfileImageKeyName(Uuid uuid) {
        return amazonConfig.getProfileImagePath() + '/' + uuid.getUuid();
    }

    public String generatePortFolioKeyName(Uuid uuid) {
        return amazonConfig.getPortfolioPath() + '/' + uuid.getUuid();
    }

    public String generatePostImageKeyName(Uuid uuid) {
        return amazonConfig.getPostImagePath() + '/' + uuid.getUuid();
    }
}
