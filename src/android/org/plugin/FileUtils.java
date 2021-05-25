package org.plugin;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {
    int pageHeight = 1120;
    int pagewidth = 792;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Uri generatePdf(Context context, String textoPdf, String nombreArchivo) throws IOException {
        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint contenido = new Paint();

        contenido.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        contenido.setTextSize(15);

        contenido.setTextAlign(Paint.Align.LEFT);

        int x = 50, y=100;
        for (String line: textoPdf.split("\n")){
            canvas.drawText(line, x, y, contenido);
            y += contenido.descent() - contenido.ascent();
        }
        pdfDocument.finishPage(page);

        return savePDFFile(context, nombreArchivo + ".pdf", pdfDocument);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri savePDFFile(final Context context,
                            final String displayName,
                            final PdfDocument pdfDocument) throws IOException {

        String relativeLocation = Environment.DIRECTORY_DOCUMENTS;
        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation + File.separator + "Banrural");
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
        final ContentResolver resolver = context.getContentResolver();
        OutputStream stream = null;
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Files.getContentUri("external");
            uri = resolver.insert(contentUri, contentValues);
            ParcelFileDescriptor pfd;
            try {
                assert uri != null;

                pfd = context.getContentResolver().openFileDescriptor(uri, "w");
                assert pfd != null;
                FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());

                pdfDocument.writeTo(out);
                out.close();
                pfd.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            contentValues.clear();
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
            context.getContentResolver().update(uri, contentValues, null, null);
            stream = resolver.openOutputStream(uri);
            if (stream == null) {
                throw new IOException("Failed to get output stream.");
            }
            return uri;
        } catch (Exception e) {
            resolver.delete(uri, null, null);
            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

}
