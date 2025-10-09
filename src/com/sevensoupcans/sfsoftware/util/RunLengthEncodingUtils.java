package com.sevensoupcans.sfsoftware.util;

import java.io.ByteArrayOutputStream;

public abstract class RunLengthEncodingUtils 
{
	public static byte[] decode(byte[] data)
	{
		if (data == null || data.length == 0)
			return new byte[0];

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = 0;

		while (i < data.length)
		{
			int b = data[i++] & 0xFF;

			// Check for run marker (0x00) and ensure two more bytes follow
			if (b == 0x00)
			{
				if (i + 1 < data.length)
				{
					int count = data[i++] & 0xFF;
					byte value = data[i++];
					for (int j = 0; j < count; j++)
						out.write(value);
				}
				else
				{
					// Malformed stream (incomplete run marker at end)
					// Write literal 0x00 to avoid data loss
					out.write(0x00);
				}
			}
			else
			{
				// Literal byte
				out.write(b);
			}
		}

		return out.toByteArray();
	}
	
	public static byte[] encode(byte[] data)
	{
		if (data == null || data.length == 0)
			return new byte[0];

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = 0;

		while (i < data.length)
		{
			byte value = data[i];
			int run = 1;

			// Count run length, capped at 255
			while (i + run < data.length && data[i + run] == value && run < 255)
			{
				run++;
			}

			if (run >= 4)
			{
				// Compressed run: marker, count, value
				out.write(0x00);
				out.write(run);
				out.write(value);
			}
			else
			{
				// Literal bytes (1–3 copies)
				for (int j = 0; j < run; j++)
				{
					// Escape literal 0x00 so decoder won’t misinterpret it as a run marker
					if (value == 0x00)
					{
						out.write(0x00);
						out.write(1);
						out.write(0x00);
					}
					else
					{
						out.write(value);
					}
				}
			}

			i += run;
		}

		return out.toByteArray();
	}

}
