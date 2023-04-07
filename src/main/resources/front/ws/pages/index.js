import PageContainer from "@/components/pageContainer"
import styles from "@/styles/Home.module.css"

export default function Home() {
  return (<PageContainer>
    <HomePage />
  </PageContainer>);
}

function HomePage() {

  const certificates = getAllCertificates();

  return <>
    <section className={styles.main_grid}>
      <div id="all_elements_table">
        <div className="toggle_options"></div>
        <div className="filter_options"></div>
        <div id="main_area">
          <table className="main_table">
            <thead>
              <tr>

              </tr>
            </thead>


          </table>
        </div>
      </div>
      <div id="one_element_preview"></div>
    </section >
  </>
}

async function getAllCertificates() {
  return [{ t: 1, q: 2 }]
}
