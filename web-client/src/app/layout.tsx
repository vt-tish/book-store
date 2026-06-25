import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { AuthProvider } from "@/common/hooks/useAuth";
import { LocaleProvider } from "@/common/hooks/useLocale";
import { ToastProvider } from "@/common/components/Toast";
import { Navbar } from "@/common/components/Navbar";

const inter = Inter({
  subsets: ["latin", "cyrillic"],
  variable: "--font-inter",
  display: "swap",
});

export const metadata: Metadata = {
  title: "BookStore — Discover & Order Books",
  description:
    "A premium online bookstore. Browse thousands of books, manage your cart and orders seamlessly.",
  keywords: ["bookstore", "books", "online shop", "reading"],
  openGraph: {
    title: "BookStore",
    description: "Discover and order your favorite books",
    type: "website",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={inter.variable}>
      <body>
        <LocaleProvider>
          <AuthProvider>
            <ToastProvider>
              <Navbar />
              <main>{children}</main>
            </ToastProvider>
          </AuthProvider>
        </LocaleProvider>
      </body>
    </html>
  );
}
