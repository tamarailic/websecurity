import PageContainer from "@/components/pageContainer"
import styles from "@/styles/Home.module.css"
import { useState } from "react";

export default function Home() {
  return (<PageContainer>
    <HomePage />
  </PageContainer>);
}

function HomePage() {
  const [optionSelected, setOptionSelected] = useState(0);
  const certificates = getAllCertificates();

  function toogle_option(i) {
    setOptionSelected(i);
  }

  return <>
    <section className={styles.main_grid}>
      <div className={styles.card} id="all_elements_table">
        <div className={styles.toggle_options}>
          <p onClick={() => toogle_option(0)} className={optionSelected == 0 ? styles.selected : ''}>All certificates</p>
          <div className={styles.separator}></div>
          <p onClick={() => toogle_option(1)} className={optionSelected == 1 ? styles.selected : ''}>My requests</p>
          <div className={styles.separator}></div>
          <p onClick={() => toogle_option(2)} className={optionSelected == 2 ? styles.selected : ''}>For signing</p>
        </div>
        <div className={styles.filter_options}></div>
        <div id="main_area">
          <table className={styles.main_table}>
            <thead>
              <tr>

              </tr>
            </thead>


          </table>
        </div>
      </div>
      <div className={styles.card} id="one_element_preview"></div>
    </section >
  </>
}

async function getAllCertificates() {
  return [{ t: 1, q: 2 }]
}
