package alifec.core.persistence.compress;

/**
 * Created by Sergio Del Castillo on 08/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompressionResult {
    private int liveTime;
    int compressedSize;
    int originalSize;
    long compressTime;

    public CompressionResult(int compressedSize, int originalSize) {
        this.compressedSize = compressedSize;
        this.originalSize = originalSize;
        this.compressTime = 0;
        this.liveTime = 0;
    }
}
